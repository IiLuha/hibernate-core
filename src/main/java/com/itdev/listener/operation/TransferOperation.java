package com.itdev.listener.operation;

import com.itdev.dto.AccountDto;
import com.itdev.exception.InsufficientFundsException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class TransferOperation extends OperationPreparer implements OperationCommand {

    public TransferOperation(AccountService accountService,
                             ParameterConsoleListener parameterConsoleListener) {
        super(accountService, ConsoleOperationType.ACCOUNT_TRANSFER, parameterConsoleListener);
    }

    @Override
    public void execute() {
        Optional<AccountDto> maybeAccount = findAccount(ConsoleOperationType.ACCOUNT_WITHDRAW);
        if (maybeAccount.isEmpty()) return;
        AccountDto accountFrom = maybeAccount.get();

        maybeAccount = findAccount(ConsoleOperationType.ACCOUNT_DEPOSIT);
        if (maybeAccount.isEmpty()) return;
        AccountDto accountTo = maybeAccount.get();

        Optional<BigDecimal> maybeAmount = getAmount();
        if (maybeAmount.isEmpty()) return;
        BigDecimal amount = maybeAmount.get();

        try {
            accountService.transfer(accountFrom.id(), accountTo.id(), amount);
            System.out.printf("Amount %s transferred from account ID %s to account ID %s.%n",
                    amount, accountFrom.id(), accountTo.id());
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
