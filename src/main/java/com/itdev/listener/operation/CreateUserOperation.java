package com.itdev.listener.operation;

import com.itdev.dao.entity.User;
import com.itdev.exception.LoginAlreadyExistException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class CreateUserOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final ParameterConsoleListener parameterConsoleListener;
    private final UserService userService;

    public CreateUserOperation(ParameterConsoleListener parameterConsoleListener, UserService userService) {
        this.parameterConsoleListener = parameterConsoleListener;
        this.userService = userService;
        this.consoleOperationType = ConsoleOperationType.USER_CREATE;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter login for new user:");
        Optional<String> maybeLogin = parameterConsoleListener.listenLogin(scanner);
        if (maybeLogin.isEmpty()) return;
        String login = maybeLogin.get();

        try {
            User user = userService.create(login);
            System.out.println("User created: " + user);
        } catch (LoginAlreadyExistException e) {
            System.out.println(e.getMessage() +
                    "Try again with another login.");
        }
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return consoleOperationType;
    }
}
