package com.itdev.listener.operation;

import com.itdev.dto.AccountDto;
import com.itdev.exception.InsufficientFundsException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class WithdrawOperation extends OperationPreparer implements OperationCommand {

    public WithdrawOperation(AccountService accountService,
                             ParameterConsoleListener parameterConsoleListener) {
        super(accountService, ConsoleOperationType.ACCOUNT_WITHDRAW, parameterConsoleListener);
    }

    @Override
    public void execute() {
        Optional<AccountDto> maybeAccount = findAccount(consoleOperationType);
        if (maybeAccount.isEmpty()) return;
        AccountDto accountFrom = maybeAccount.get();

        Optional<BigDecimal> maybeAmount = getAmount();
        if (maybeAmount.isEmpty()) return;
        BigDecimal amount = maybeAmount.get();

        try {
            AccountDto withdrawn = accountService.withdraw(accountFrom.id(), amount);
            System.out.printf("Amount %s was withdrawn from account ID: %s%n",
                    amount, withdrawn.id());
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage() +
                    System.lineSeparator() +
                    "There are %s funds available.".formatted(e.getAvailableFunds()));
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return consoleOperationType;
    }
}
