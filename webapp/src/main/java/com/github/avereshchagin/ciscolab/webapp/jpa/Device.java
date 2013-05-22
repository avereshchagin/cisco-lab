package com.github.avereshchagin.ciscolab.webapp.jpa;

import javax.persistence.*;

@Entity
@Table(name = "devices")
@NamedQuery(name = "allDevices", query = "SELECT d FROM Device d")
public class Device {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rack_id", nullable = false)
    private Rack rack;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false, length = 12)
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
