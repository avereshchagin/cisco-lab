package com.github.avereshchagin.ciscolab.webapp.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trainers")
public class Trainer extends User {

    @Column(nullable = false)
    @OneToOne
    private Lab lab;

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }
}
