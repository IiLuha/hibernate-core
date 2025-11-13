package com.itdev.dao.repository;

import com.itdev.dao.entity.User;
import jakarta.persistence.EntityManager;
import org.hibernate.graph.GraphSemantic;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User create(User user) {
        Optional.of(user).ifPresent(entityManager::persist);
        return user;
    }

    public User saveAnFlush(User user) {
        Optional.of(user).ifPresent(entityManager::persist);
        entityManager.flush();
        return user;
    }

    public List<User> findAllWithAccounts() {
        return entityManager.createQuery("select u from User u", User.class)
                .setHint(GraphSemantic.LOAD.getJakartaHintName(),
                        entityManager.getEntityGraph("withAccounts"))
                .getResultList();
    }

    public Optional<User> findById(Integer id) {
        return Optional.of(id)
                .map(userId -> entityManager.find(User.class, userId));
    }

    public Optional<User> findByLogin(String maybeUniqueLogin) {
        return entityManager.createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", maybeUniqueLogin)
                .getResultStream().findFirst();
    }
}
