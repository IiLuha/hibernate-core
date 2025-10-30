package com.itdev.listener;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ParameterConsoleListener {

    public Optional<String> listenLogin(Scanner scanner) {
        if (scanner.hasNext()) return Optional.of(scanner.nextLine());
        return Optional.empty();
    }

    public Optional<Integer> listenId(Scanner scanner) {
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

    public Optional<BigDecimal> listenAmount(Scanner scanner) {
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
