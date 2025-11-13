package com.itdev.dto;

import java.util.List;

public record UserDto(
        Integer id,
        String login,
        List<AccountDto> accounts
) {
}
