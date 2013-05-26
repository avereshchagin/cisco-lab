package com.github.avereshchagin.ciscolab.webapp.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
public class Administrator extends User {
}
