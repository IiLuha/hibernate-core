package com.itdev.service;

import com.itdev.dao.entity.Account;
import com.itdev.dao.entity.User;
import com.itdev.dao.repository.AccountRepository;
import com.itdev.dao.repository.UserRepository;
import com.itdev.exception.LoginAlreadyExistException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final IdSequence idSequence;

    public UserService(AccountService accountService, AccountRepository accountRepository, UserRepository userRepository, IdSequence idSequence) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.idSequence = idSequence;
    }

    public User create(String login) {
        String newLogin = Optional.of(login)
                .filter(userRepository::loginNotContained)
                .orElseThrow(() -> new LoginAlreadyExistException("Login " + login + " already exist."));
        User user = new User(idSequence.generateNextId(),
                newLogin);
        Account acc = accountService.getDefaultAcc(user.getId());
        user.addAccount(acc);
        accountRepository.create(acc);
        return userRepository.create(user);

    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
