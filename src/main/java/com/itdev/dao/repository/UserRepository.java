package com.itdev.dao.repository;

import com.itdev.dao.entity.User;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserRepository {

    private final Set<String> logins;
    private final Map<Integer, User> users;

    public UserRepository() {
        this.logins = new HashSet<>();
        this.users = new HashMap<>();
    }

    public User create(User user) {
        Optional.of(user)
                .map(u -> {
                    users.put(u.getId(), u);
                    return u.getLogin();
                })
                .map(logins::add);
        return user;
    }

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public Optional<User> findById(Integer id) {
        return Optional.of(id)
                .map(users::get);
    }

    public boolean loginNotContained(String login) {
        return !logins.contains(login);
    }
}
