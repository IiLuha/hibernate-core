package com.itdev.service;

import com.itdev.dao.entity.User;
import com.itdev.dao.repository.UserRepository;
import com.itdev.dto.UserDto;
import com.itdev.exception.LoginAlreadyExistException;
import com.itdev.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;

@Component
public class UserService {

    private final AccountService accountService;
    private final UserRepository userRepository;
    private final TransactionHelper transactionHelper;
    private final UserMapper userMapper;

    public UserService(AccountService accountService,
                       UserRepository userRepository,
                       TransactionHelper transactionHelper, UserMapper userMapper) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.transactionHelper = transactionHelper;
        this.userMapper = userMapper;
    }

    public UserDto create(String login) {
        String newLogin = Optional.of(login)
                .filter(this::loginDoesNotExist)
                .orElseThrow(() -> new LoginAlreadyExistException("Login " + login + " already exist."));
        return Optional.of(newLogin)
                .map(User::new)
                .map(user -> (Supplier<User>) () -> {
                    User persistedUser = userRepository.saveAnFlush(user);
                    accountService.create(persistedUser.getId());
                    return persistedUser;
                })
                .map(transactionHelper::executeInTransaction)
                .map(userMapper::map)
                .orElseThrow();

    }

    public boolean loginDoesNotExist(String login) {
        return Optional.of(login)
                .map(maybeUniqueLogin -> (Supplier<Optional<User>>) () -> userRepository.findByLogin(maybeUniqueLogin))
                .flatMap(transactionHelper::executeInTransaction)
                .isEmpty();
    }

    public List<User> findAllWithAccounts() {
        return transactionHelper.executeInTransactionListResult(userRepository::findAllWithAccounts);
    }
}
