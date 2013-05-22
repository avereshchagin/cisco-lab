package com.github.avereshchagin.ciscolab.webapp.jpa;

import javax.persistence.*;

@Entity
@Table(name = "racks")
@NamedQueries({
        @NamedQuery(name = "Rack.findAll", query = "SELECT r from Rack r"),
        @NamedQuery(name = "Rack.deleteDevices", query = "DELETE FROM Device d WHERE d.rack = :rack")
})
public class Rack {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String name;

    @Column(nullable = false, length = 15)
    private String localIP;

    @Column(nullable = false)
    private Integer localTerminalPort;

    @Column(nullable = false)
    private Integer localControlPort;

    @Column(nullable = false, length = 15)
    private String externalIP;

    @Column(nullable = false)
    private Integer externalTerminalPort;

    @Column(nullable = false)
    private Integer externalControlPort;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalIP() {
        return localIP;
    }

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public int getLocalTerminalPort() {
        return localTerminalPort;
    }

    public void setLocalTerminalPort(int localTerminalIP) {
        this.localTerminalPort = localTerminalIP;
    }

    public int getLocalControlPort() {
        return localControlPort;
    }

    public void setLocalControlPort(int localControlIP) {
        this.localControlPort = localControlIP;
    }

    public String getExternalIP() {
        return externalIP;
    }

    public void setExternalIP(String externalIP) {
        this.externalIP = externalIP;
    }

    public int getExternalTerminalPort() {
        return externalTerminalPort;
    }

    public void setExternalTerminalPort(int externalTerminalIP) {
        this.externalTerminalPort = externalTerminalIP;
    }

    public int getExternalControlPort() {
        return externalControlPort;
    }

    public void setExternalControlPort(int externalControlIP) {
        this.externalControlPort = externalControlIP;
    }
}
