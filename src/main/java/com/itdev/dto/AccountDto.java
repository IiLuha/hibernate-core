package com.itdev.dto;

import java.math.BigDecimal;

public record AccountDto(
        Integer id,
        Integer userId,
        BigDecimal moneyAmount
) {
}
