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
public class WithdrawOperation implements OperationCommand {

    private final ConsoleOperationType consoleOperationType;
    private final ParameterConsoleListener parameterConsoleListener;
    private final AccountService accountService;

    public WithdrawOperation(ParameterConsoleListener parameterConsoleListener, AccountService accountService) {
        this.parameterConsoleListener = parameterConsoleListener;
        this.accountService = accountService;
        this.consoleOperationType = ConsoleOperationType.ACCOUNT_WITHDRAW;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter account ID:");
        Optional<Integer> maybeId = parameterConsoleListener.listenId(scanner);
        if (maybeId.isEmpty()) return;
        Integer id = maybeId.get();

        System.out.println("Enter amount to withdraw:");
        Optional<BigDecimal> maybeAmount = parameterConsoleListener.listenAmount(scanner);
        if (maybeAmount.isEmpty()) return;
        BigDecimal amount = maybeAmount.get();

        try {
            accountService.withdraw(id, amount);
            System.out.printf("Amount %s was withdrawn from account ID: %s%n", amount, id);
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
