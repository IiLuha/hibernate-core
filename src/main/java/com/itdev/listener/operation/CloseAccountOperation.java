package com.itdev.listener.operation;

import com.itdev.dto.AccountDto;
import com.itdev.exception.DeleteFirstAccountException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CloseAccountOperation extends OperationPreparer implements OperationCommand {

    public CloseAccountOperation(AccountService accountService,
                                 ParameterConsoleListener parameterConsoleListener) {
        super(accountService, ConsoleOperationType.ACCOUNT_CLOSE, parameterConsoleListener);
    }

    @Override
    public void execute() {
        Optional<AccountDto> maybeAccount = findAccount(consoleOperationType);
        if (maybeAccount.isEmpty()) return;
        AccountDto account = maybeAccount.get();

        try {
            accountService.delete(account.id());
            System.out.printf("The account with ID %s has been successfully deleted.%n", account.id());
        } catch (DeleteFirstAccountException e) {
            System.out.println(e.getMessage() +
                    System.lineSeparator() +
                    "Try again with another account id.");
        }

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return consoleOperationType;
    }
}
