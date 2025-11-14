package com.itdev.listener.operation;

import com.itdev.dto.AccountDto;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class DepositOperation extends OperationPreparer implements OperationCommand {

    public DepositOperation(AccountService accountService,
                            ParameterConsoleListener parameterConsoleListener) {
        super(accountService, ConsoleOperationType.ACCOUNT_DEPOSIT, parameterConsoleListener);
    }

    @Override
    public void execute() {
        Optional<AccountDto> maybeAccount = findAccount(consoleOperationType);
        if (maybeAccount.isEmpty()) return;
        AccountDto accountTo = maybeAccount.get();

        Optional<BigDecimal> maybeAmount = getAmount();
        if (maybeAmount.isEmpty()) return;
        BigDecimal amount = maybeAmount.get();

        AccountDto deposited = accountService.deposit(accountTo.id(), amount);
        System.out.printf("Amount %s deposited to account ID: %s%n", amount, deposited.id());
        System.out.printf("There are %s funds available.%n", deposited.moneyAmount());

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return consoleOperationType;
    }
}
