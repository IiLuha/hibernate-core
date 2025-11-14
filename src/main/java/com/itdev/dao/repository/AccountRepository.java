package com.itdev.dao.repository;

import com.itdev.dao.entity.Account;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountRepository {

    private final EntityManager entityManager;

    public AccountRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Account> findById(Integer id) {
        return Optional.of(id)
                .map(accId -> entityManager.find(Account.class, accId));
    }

    public Account create(Account account) {
        Optional.of(account)
                .ifPresent(entityManager::persist);
        return account;
    }

    public void delete(Account account) {
        Optional.of(account)
                .ifPresent(entityManager::remove);
    }

    public Account update(Account account) {
        return Optional.of(account)
                .map(entityManager::merge)
                .orElseThrow();
    }
}

