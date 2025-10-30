package com.itdev.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    private BigDecimal availableFunds;

    public InsufficientFundsException(String message, BigDecimal availableFunds) {
        super(message);
        this.availableFunds = availableFunds;
    }

    public BigDecimal getAvailableFunds() {
        return availableFunds;
    }
}
