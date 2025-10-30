package com.itdev.listener.operation;

import com.itdev.exception.AccountNotFoundException;
import com.itdev.exception.DeleteFirstAccountException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class CloseAccountOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final ParameterConsoleListener parameterConsoleListener;
    private final AccountService accountService;

    public CloseAccountOperation(ParameterConsoleListener parameterConsoleListener, AccountService accountService) {
        this.parameterConsoleListener = parameterConsoleListener;
        this.accountService = accountService;
        this.consoleOperationType = ConsoleOperationType.ACCOUNT_CLOSE;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter account ID to close:");
        Optional<Integer> maybeId = parameterConsoleListener.listenId(scanner);
        if (maybeId.isEmpty()) return;
        Integer id = maybeId.get();

        try {
            accountService.delete(id);
            System.out.printf("The account with ID %s has been successfully deleted.%n", id);
        } catch (AccountNotFoundException | DeleteFirstAccountException e) {
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
