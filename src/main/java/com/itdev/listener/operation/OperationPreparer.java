package com.itdev.listener.operation;

import com.itdev.dto.AccountDto;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;

import java.math.BigDecimal;
import java.util.Optional;

public abstract class OperationPreparer {

    protected final AccountService accountService;
    protected final ConsoleOperationType consoleOperationType;
    protected final ParameterConsoleListener parameterConsoleListener;

    public OperationPreparer(AccountService accountService,
                             ConsoleOperationType consoleOperationType,
                             ParameterConsoleListener parameterConsoleListener) {
        this.accountService = accountService;
        this.consoleOperationType = consoleOperationType;
        this.parameterConsoleListener = parameterConsoleListener;
    }

    protected Optional<AccountDto> findAccount(ConsoleOperationType consoleOperationType) {
        String message = "Enter %s account ID to %s:%n";
        String whichAccount = "";
        switch (consoleOperationType) {
            case ACCOUNT_CLOSE, ACCOUNT_DEPOSIT -> whichAccount = "target";
            case ACCOUNT_WITHDRAW -> whichAccount = "source";
            default -> {return Optional.empty();}
        }
        System.out.printf(message, whichAccount, this.consoleOperationType.getOperationName());
        Optional<Integer> maybeId = parameterConsoleListener.listenId();
        if (maybeId.isEmpty()) return Optional.empty();
        return maybeId.flatMap(accountService::findById)
                .or(() -> {
                    System.out.printf(
                            "Account with id %s does not exist.%nTry again with another account id.%n",
                            maybeId.get()
                    );
                    return Optional.empty();
                });
    }

    protected Optional<BigDecimal> getAmount() {
        System.out.printf("Enter amount to %s:%n", this.consoleOperationType.getOperationName());
        return parameterConsoleListener.listenAmount();
    }
}

