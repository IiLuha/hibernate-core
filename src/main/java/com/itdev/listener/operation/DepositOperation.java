package com.itdev.listener.operation;

import com.itdev.exception.AccountNotFoundException;
import com.itdev.exception.DeleteFirstAccountException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

@Component
public class DepositOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final ParameterConsoleListener parameterConsoleListener;
    private final AccountService accountService;

    public DepositOperation(ParameterConsoleListener parameterConsoleListener, AccountService accountService) {
        this.parameterConsoleListener = parameterConsoleListener;
        this.accountService = accountService;
        this.consoleOperationType = ConsoleOperationType.ACCOUNT_DEPOSIT;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter account ID:");
        Optional<Integer> maybeId = parameterConsoleListener.listenId(scanner);
        if (maybeId.isEmpty()) return;
        Integer id = maybeId.get();

        System.out.println("Enter amount to deposit:");
        Optional<BigDecimal> maybeAmount = parameterConsoleListener.listenAmount(scanner);
        if (maybeAmount.isEmpty()) return;
        BigDecimal amount = maybeAmount.get();

        try {
            accountService.deposit(id, amount);
            System.out.printf("Amount %s deposited to account ID: %s%n", amount, id);
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
