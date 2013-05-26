package com.github.avereshchagin.ciscolab.webapp.jpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student extends User {

    @Column(nullable = false)
    @ManyToOne
    private Lab lab;

    @Column(nullable = false)
    @ElementCollection
    private List<String> deviceNames = new ArrayList<>();

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public List<String> getDeviceNames() {
        return deviceNames;
    }

    public void setDeviceNames(List<String> deviceNames) {
        this.deviceNames = deviceNames;
    }
}
