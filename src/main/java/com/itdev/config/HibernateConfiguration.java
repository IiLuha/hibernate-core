package com.itdev.config;

import com.itdev.dao.entity.Account;
import com.itdev.dao.entity.User;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory configureSessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

        configuration
                .addPackage("com.itdev")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setJdbcUrl("jdbc:postgresql://localhost:5432/postgres")
                .setCredentials("postgres", "postgre")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.current_session_context_class", "thread")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class);

        return configuration.buildSessionFactory();
    }

    @Bean
    public EntityManager entityManager() {
        SessionFactory sessionFactory = configureSessionFactory();
        return (Session) Proxy.newProxyInstance(sessionFactory.getClass().getClassLoader(), new Class[]{Session.class},
                (proxy, method, args1) -> method.invoke(sessionFactory.getCurrentSession(), args1));
    }
}
