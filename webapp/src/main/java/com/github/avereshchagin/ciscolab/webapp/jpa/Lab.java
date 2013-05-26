package com.github.avereshchagin.ciscolab.webapp.jpa;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "labs")
@NamedQueries({
        @NamedQuery(name = "Lab.findAll", query = "SELECT l FROM Lab l")
})
public class Lab {

    @Expose
    @Id
    @GeneratedValue
    private Long id;

    @Expose
    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @Expose
    @OneToOne(mappedBy = "lab")
    private Trainer trainer;

    @OneToMany(mappedBy = "lab")
    List<Student> students = new ArrayList<>();

    @Column(nullable = false, length = 4096)
    String topology;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getTopology() {
        return topology;
    }

    public void setTopology(String topology) {
        this.topology = topology;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
