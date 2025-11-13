package com.itdev.listener;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ParameterConsoleListener {

    private final Scanner scanner;

    public ParameterConsoleListener(Scanner scanner) {
        this.scanner = scanner;
    }

    public Optional<String> listenLogin() {
        System.out.println("Enter login for new user:");
        if (scanner.hasNext()) return Optional.of(scanner.nextLine());
        return Optional.empty();
    }

    public Optional<Integer> listenId() {
        Optional<Integer> id;
        if (scanner.hasNext()) {
            String maybeId = scanner.nextLine();
            try {
                id = Optional.of(Integer.parseInt(maybeId));
            } catch (NumberFormatException e) {
                System.out.println("Invalid id. Please enter a positive integer.");
                return Optional.empty();
            }
            if (id.get() < 1) {
                System.out.println("Invalid id. Please enter a positive integer.");
                return Optional.empty();
            }
            return id;
        }
        return Optional.empty();
    }

    public Optional<BigDecimal> listenAmount() {
        Optional<BigDecimal> amount;
        if (scanner.hasNext()) {
            String maybeAmount = scanner.nextLine();
            try {
                amount = Optional.of(new BigDecimal(maybeAmount));
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a positive number.");
                return Optional.empty();
            }
            if (amount.get().compareTo(BigDecimal.ONE) < 0) {
                System.out.println("Invalid amount. Please enter a positive number.");
                return Optional.empty();
            }
            return amount;
        }
        return Optional.empty();
    }
}
