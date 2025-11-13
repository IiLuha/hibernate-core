package com.itdev.mapper;

import com.itdev.dao.entity.User;
import com.itdev.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {

    private final AccountMapper accountMapper;

    public UserMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public UserDto map(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getAccounts().stream()
                        .map(accountMapper::map)
                        .toList()
        );
    }
}
