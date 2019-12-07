package com.amigox.todo.Entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.*;

@Data

@Entity
@Table (name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int user_id;
    String firstName;
    String lastName;
    String username;
    @OneToOne(mappedBy = "user")
    Role role = new Role();
    @Column(unique = true)
    String email;
    String password;
    @OneToMany(mappedBy = "user", cascade=CascadeType.PERSIST)
    Set<Todo> todos = new HashSet<>();
    @Temporal(TemporalType.DATE)
    Date joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = new Date();
    }
}
