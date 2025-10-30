package com.itdev.listener.operation;

import com.itdev.dao.entity.Account;
import com.itdev.exception.UserNotFoundException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class CreateAccountOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final ParameterConsoleListener parameterConsoleListener;
    private final AccountService accountService;

    public CreateAccountOperation(ParameterConsoleListener parameterConsoleListener, AccountService accountService) {
        this.parameterConsoleListener = parameterConsoleListener;
        this.accountService = accountService;
        this.consoleOperationType = ConsoleOperationType.ACCOUNT_CREATE;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter the user id for which to create an account:");
        Optional<Integer> maybeId = parameterConsoleListener.listenId(scanner);
        if (maybeId.isEmpty()) return;
        Integer id = maybeId.get();

        try {
            Account account = accountService.create(id);
            System.out.println(account + " was created");
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage() +
                    System.lineSeparator() +
                    "Try again with another user id.");
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return consoleOperationType;
    }
}
