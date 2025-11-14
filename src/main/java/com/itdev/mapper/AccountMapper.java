package com.itdev.mapper;

import com.itdev.dao.entity.Account;
import com.itdev.dto.AccountDto;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper implements Mapper<Account, AccountDto> {

    @Override
    public AccountDto map(Account acc) {
        return new AccountDto(
                acc.getId(),
                acc.getUser().getId(),
                acc.getMoneyAmount()
        );
    }
}
