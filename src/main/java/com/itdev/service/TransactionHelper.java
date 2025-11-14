package com.itdev.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class TransactionHelper {

    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        boolean transactionStarted = false;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            transactionStarted = true;
        }
        try {
            T result = action.get();
            if (transactionStarted) transaction.commit();
            return result;
        } catch (Exception exception) {
            if (transactionStarted) transaction.rollback();
            throw exception;
        }
    }

    public <T> void executeInTransaction(Runnable action) {
        boolean transactionStarted = false;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            transactionStarted = true;
        }
        try {
            action.run();
            if (transactionStarted) transaction.commit();
        } catch (Exception exception) {
            if (transactionStarted) transaction.rollback();
            throw exception;
        }
    }

    public <T> List<T> executeInTransactionListResult(Supplier<List<T>> action) {
        boolean transactionStarted = false;
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            transactionStarted = true;
        }
        try {
            List<T> result = action.get();
            if (transactionStarted) transaction.commit();
            return result;
        } catch (Exception exception) {
            if (transactionStarted) transaction.rollback();
            throw exception;
        }
    }
}
