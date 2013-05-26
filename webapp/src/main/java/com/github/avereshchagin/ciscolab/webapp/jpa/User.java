package com.github.avereshchagin.ciscolab.webapp.jpa;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
@SecondaryTable(name = "access_tokens")
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "User.verify", query = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password"),
        @NamedQuery(name = "User.find", query = "SELECT u FROM User u WHERE u.login = :login")
})
public abstract class User {

    @Expose
    @Id
    @GeneratedValue
    private Long id;

    @Expose
    @Column(nullable = false, updatable = false, unique = true, length = 16)
    private String login;

    @Expose
    @Column(nullable = false, length = 16)
    private String password;

    @Expose
    @Column(nullable = false, length = 32)
    private String displayName;

    @Column(table = "access_tokens", nullable = true, length = 32)
    private String accessToken;

    @Column(table = "access_tokens", nullable = true)
    private Date accessTokenExpires;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getAccessTokenExpires() {
        return accessTokenExpires;
    }

    public void setAccessTokenExpires(Date accessTokenExpires) {
        this.accessTokenExpires = accessTokenExpires;
    }
}
