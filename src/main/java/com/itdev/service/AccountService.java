package com.itdev.service;

import com.itdev.dao.entity.Account;
import com.itdev.dao.entity.User;
import com.itdev.dao.repository.AccountRepository;
import com.itdev.dao.repository.UserRepository;
import com.itdev.dto.AccountDto;
import com.itdev.exception.AccountNotFoundException;
import com.itdev.exception.DeleteFirstAccountException;
import com.itdev.exception.InsufficientFundsException;
import com.itdev.exception.UserNotFoundException;
import com.itdev.mapper.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class AccountService {

    private static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal(100);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionHelper transactionHelper;
    private final Mapper<Account, AccountDto> accountMapper;
    private final BigDecimal DEFAULT_AMOUNT;
    private final BigDecimal TRANSFER_COMMISSION;

    public AccountService(UserRepository userRepository,
                          AccountRepository accountRepository,
                          TransactionHelper transactionHelper,
                          Mapper<Account, AccountDto> accountMapper,
                          @Value("${account.default-amount}") String DEFAULT_AMOUNT,
                          @Value("${account.transfer-commission}") String transferCommission) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionHelper = transactionHelper;
        this.accountMapper = accountMapper;
        this.DEFAULT_AMOUNT = new BigDecimal(DEFAULT_AMOUNT).setScale(2, RoundingMode.HALF_UP);
        this.TRANSFER_COMMISSION = new BigDecimal(transferCommission).setScale(2, RoundingMode.HALF_UP)
                .divide(ONE_HUNDRED_PERCENT, RoundingMode.HALF_UP);
    }

    private Account getDefaultAcc(User user) {
        return new Account(user, DEFAULT_AMOUNT);
    }

    public AccountDto create(Integer userId) {
        User user = Optional.of(userId)
                .map(uId -> (Supplier<Optional<User>>) () -> userRepository.findById(uId))
                .flatMap(transactionHelper::executeInTransaction)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist."));
        return Optional.of(getDefaultAcc(user)).stream()
                .peek(user::addAccount)
                .map(acc -> (Supplier<Account>) () -> accountRepository.create(acc))
                .map(transactionHelper::executeInTransaction)
                .findFirst()
                .map(accountMapper::map).orElseThrow();
    }

    public void delete(Integer id) {
        Account account = getAccount(id);

        Integer userId = account.getUser().getId();
        User user = Optional.of(userId)
                .map(uId -> (Supplier<Optional<User>>) () -> userRepository.findById(uId))
                .flatMap(transactionHelper::executeInTransaction)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist."));

        List<Account> accounts = user.getAccounts();
        if (accounts.isEmpty()) throw new RuntimeException("The account " + id + " is associated with a user " +
                user.getId() + " that does not contain any accounts.");
        Account firstAccount = getFirstAccount(accounts);
        if (firstAccount.equals(account)) throw new DeleteFirstAccountException();
        transactionHelper.executeInTransaction(() -> {
            deposit(firstAccount, account.getMoneyAmount());
            accounts.remove(account);
            accountRepository.delete(account);
        });
    }

    private Account getFirstAccount(List<Account> accounts) {
        return accounts.stream()
                .min(Comparator.comparing(Account::getId))
                .orElseThrow();
    }

    public void transfer(Integer idFrom, Integer idTo, BigDecimal amount) {
        Account accountFrom = Optional.of(idFrom).map(this::getAccount)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + idFrom + " does not exist."));
        Account accountTo = Optional.of(idTo).map(this::getAccount)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + idTo + " does not exist."));
        if (idFrom.equals(idTo)) {
            transferWithoutCommission(accountFrom, accountTo, amount);
        } else {
            transferWithCommission(accountFrom, accountTo, amount);
        }
    }

    private Account getAccount(Integer id) {
        return Optional.of(id)
                .map(accId -> (Supplier<Optional<Account>>) () -> accountRepository.findById(accId))
                .flatMap(transactionHelper::executeInTransaction)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " does not exist."));
    }

    private void transferWithoutCommission(Account from, Account to, BigDecimal amount) {
        transfer(from, to, amount, amount);
    }

    private void transferWithCommission(Account from, Account to, BigDecimal amount) {
        BigDecimal toDeposit = amount.multiply(BigDecimal.ONE.subtract(TRANSFER_COMMISSION));
        transfer(from, to, amount, toDeposit);
    }

    private void transfer(Account from, Account to, BigDecimal amountFrom, BigDecimal amountTo) {
        withdraw(from, amountFrom);
        deposit(to, amountTo);
    }

    public AccountDto withdraw(Integer accountId, BigDecimal amount) {
        return withdraw(getAccount(accountId), amount);
    }

    private AccountDto withdraw(Account account, BigDecimal amount) {
        BigDecimal accAmount = account.getMoneyAmount();
        Account validAcc = Optional.of(account)
                .filter(acc -> acc.getMoneyAmount().compareTo(amount) >= 0)
                .orElseThrow(() -> new InsufficientFundsException(
                        "There is insufficient funds in account with id %s to process the transaction."
                                .formatted(account.getId()), accAmount));
        return Optional.of(amount)
                .map(validAcc.getMoneyAmount()::subtract)
                .map(newAmount -> {
                    validAcc.setMoneyAmount(newAmount);
                    return validAcc;
                })
                .map(acc -> (Supplier<Account>) () -> accountRepository.update(acc))
                .map(transactionHelper::executeInTransaction)
                .map(accountMapper::map)
                .orElseThrow();
    }

    public Optional<AccountDto> findById(Integer id) {
        return Optional.of(id)
                .map(accId -> (Supplier<Optional<Account>>) () -> accountRepository.findById(accId))
                .flatMap(transactionHelper::executeInTransaction)
                .map(accountMapper::map);
    }

    public AccountDto deposit(Integer accountId, BigDecimal amount) {
        return Optional.of(accountId)
                .map(this::getAccount)
                .map(acc -> deposit(acc, amount))
                .orElseThrow();
    }

    private AccountDto deposit(Account account, BigDecimal amount) {
        return Optional.of(account).stream()
                .peek(acc -> Optional.of(amount)
                        .map(acc.getMoneyAmount()::add)
                        .ifPresent(acc::setMoneyAmount)).findFirst()
                .map(acc -> (Supplier<Account>) () -> accountRepository.update(acc))
                .map(transactionHelper::executeInTransaction)
                .map(accountMapper::map)
                .orElseThrow();
    }
}
