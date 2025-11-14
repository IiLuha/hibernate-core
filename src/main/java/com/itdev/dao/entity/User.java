package com.itdev.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NamedEntityGraph(name = "withAccounts",
        attributeNodes = @NamedAttributeNode("accounts")
)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "login", unique = true)
    String login;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    List<Account> accounts;

    public User() {
    }

    public User(String login) {
        this.login = login;
        accounts = new ArrayList<>();
    }

    public User(Integer id, String login) {
        this.id = id;
        this.login = login;
        accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        account.setUser(this);
        accounts.add(account);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(login);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", accounts=" + accounts +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
