package com.amigox.todo.Entity;

import lombok.Data;

import javax.persistence.*;

@Data

@Entity
@Table (name = "roles")

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int role_id;
    @OneToOne(fetch = FetchType.LAZY)
    User user;
    String name;
}
