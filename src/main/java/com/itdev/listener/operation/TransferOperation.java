package com.itdev.listener.operation;

import com.itdev.exception.AccountNotFoundException;
import com.itdev.exception.DeleteFirstAccountException;
import com.itdev.exception.InsufficientFundsException;
import com.itdev.listener.ParameterConsoleListener;
import com.itdev.service.AccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

@Component
public class TransferOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final ParameterConsoleListener parameterConsoleListener;
    private final AccountService accountService;

    public TransferOperation(ParameterConsoleListener parameterConsoleListener, AccountService accountService) {
        this.parameterConsoleListener = parameterConsoleListener;
        this.accountService = accountService;
        this.consoleOperationType = ConsoleOperationType.ACCOUNT_TRANSFER;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter source account ID:");
        Optional<Integer> maybeId = parameterConsoleListener.listenId(scanner);
        if (maybeId.isEmpty()) return;
        Integer idFrom = maybeId.get();

        System.out.println("Enter target account ID:");
        maybeId = parameterConsoleListener.listenId(scanner);
        if (maybeId.isEmpty()) return;
        Integer idTo = maybeId.get();

        System.out.println("Enter amount to transfer:");
        Optional<BigDecimal> maybeAmount = parameterConsoleListener.listenAmount(scanner);
        if (maybeAmount.isEmpty()) return;
        BigDecimal amount = maybeAmount.get();

        try {
            accountService.transferByIds(idFrom, idTo, amount);
            System.out.printf("Amount %s transferred from account ID %s to account ID %s.%n", amount, idFrom, idTo);
        } catch (AccountNotFoundException | DeleteFirstAccountException e) {
            System.out.println(e.getMessage() +
                    System.lineSeparator() +
                    "Try again with another account id.");
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
