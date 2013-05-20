package com.github.avereshchagin.ciscolab.webapp.jpa;

import javax.persistence.*;

@Entity
@Table(name = "users")
@NamedQuery(name = "verifyUser", query = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, updatable = false, unique = true, length = 16)
    private String login;

    @Column(nullable = false, length = 16)
    private String password;

    @Column(nullable = false, length = 32)
    private String displayName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }
}
