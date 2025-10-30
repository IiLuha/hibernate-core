package com.itdev.service;

import com.itdev.dao.entity.Account;
import com.itdev.dao.entity.User;
import com.itdev.dao.repository.AccountRepository;
import com.itdev.dao.repository.UserRepository;
import com.itdev.exception.AccountNotFoundException;
import com.itdev.exception.DeleteFirstAccountException;
import com.itdev.exception.InsufficientFundsException;
import com.itdev.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
public class AccountService {

    private static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal(100);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final IdSequence idSequence;
    private final BigDecimal DEFAULT_AMOUNT;
    private final BigDecimal TRANSFER_COMMISSION;

    public AccountService(UserRepository userRepository,
                          AccountRepository accountRepository,
                          IdSequence idSequence,
                          @Value("${account.default-amount}") String DEFAULT_AMOUNT,
                          @Value("${account.transfer-commission}") String transferCommission) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.idSequence = idSequence;
        this.DEFAULT_AMOUNT = new BigDecimal(DEFAULT_AMOUNT).setScale(2, RoundingMode.HALF_UP);
        this.TRANSFER_COMMISSION = new BigDecimal(transferCommission).setScale(2, RoundingMode.HALF_UP)
                .divide(ONE_HUNDRED_PERCENT, RoundingMode.HALF_UP);
    }

    public Account getDefaultAcc(Integer userId) {
        return new Account(idSequence.generateNextId(),
                userId,
                DEFAULT_AMOUNT);
    }

    public Account create(Integer userId) {
        User user = Optional.of(userId)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " does not exist."));
        return Optional.of(getDefaultAcc(userId)).stream()
                .peek(user::addAccount)
                .map(accountRepository::create)
                .findFirst().orElseThrow();
    }

    public Account delete(Integer id) {
        Account account = Optional.of(id)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " does not exist."));
        User user = userRepository.findById(account.getUserId())
                .orElseThrow(() -> new RuntimeException("User with id " + account.getUserId() + " does not exist."));
        List<Account> accounts = user.getAccounts();
        if (accounts.size() == 0) throw new RuntimeException("The account " + id + " is associated with a user " +
                user.getId() + " that does not contain any accounts.");
        Account firstAccount = accounts.get(0);
        if (firstAccount.equals(account)) throw new DeleteFirstAccountException();
        deposit(firstAccount, account.getMoneyAmount());
        accounts.remove(account);
        accountRepository.delete(account);
        return account;
    }

    public void transferByIds(Integer idFrom, Integer idTo, BigDecimal amount) {
        Account accountFrom = Optional.of(idFrom)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new AccountNotFoundException("Source account with id " + idFrom + " does not exist."));
        Account accountTo = Optional.of(idTo)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new AccountNotFoundException("Target account with id " + idTo + " does not exist."));
        if (accountFrom.getUserId().equals(accountTo.getUserId())) {
            transferWithoutCommission(accountFrom, accountTo, amount);
        } else {
            transferWithCommission(accountFrom, accountTo, amount);
        }
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

    public void withdraw(Integer id, BigDecimal amount) {
        Account account = Optional.of(id)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " does not exist."));
        withdraw(account, amount);
    }

    private void withdraw(Account account, BigDecimal amount) {
        BigDecimal accAmount = account.getMoneyAmount();
        if (accAmount.compareTo(amount) >= 0) {
            account.setMoneyAmount(accAmount.subtract(amount));
        } else {
            throw new InsufficientFundsException(
                    "There is insufficient funds in account with id %s to process the transaction."
                            .formatted(account.getId()), accAmount);
        }
    }

    public void deposit(Integer id, BigDecimal amount) {
        Account account = Optional.of(id)
                .flatMap(accountRepository::findById)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " does not exist."));
        deposit(account, amount);
    }

    private void deposit(Account account, BigDecimal amount) {
        account.setMoneyAmount(account.getMoneyAmount().add(amount));
    }
}
