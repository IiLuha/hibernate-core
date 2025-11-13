package com.itdev.listener;

import com.itdev.listener.operation.ConsoleOperationType;
import com.itdev.listener.operation.OperationCommand;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Component
public class OperationsConsoleListener {

    private static final String MAIN_MESSAGE = """
            Please enter one of operation type:
            -ACCOUNT_CREATE
            -SHOW_ALL_USERS
            -ACCOUNT_CLOSE
            -ACCOUNT_WITHDRAW
            -ACCOUNT_DEPOSIT
            -ACCOUNT_TRANSFER
            -USER_CREATE
            """;

    private final Map<ConsoleOperationType, OperationCommand> operations;
    private final Scanner scanner;

    public OperationsConsoleListener(List<OperationCommand> commands, Scanner scanner) {
        this.scanner = scanner;
        operations = new HashMap<>();
        commands.forEach(command -> operations.put(command.getOperationType(), command));
    }

    public void doListen() {
        System.out.println(MAIN_MESSAGE);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            Optional<ConsoleOperationType> maybeOperationType = extractOperation(input);
            if (maybeOperationType.isPresent()) {
                OperationCommand operation = operations.get(maybeOperationType.get());
                operation.execute();
            } else {
                System.out.println("Unsupported operation. Try again with the operation from the list.");
            }
            System.out.println(MAIN_MESSAGE);
        }
    }

    private Optional<ConsoleOperationType> extractOperation(String inputString) {
        return Arrays.stream(ConsoleOperationType.values())
                .filter(op -> op.name().equals(inputString))
                .findFirst();
    }
}
