package com.amigox.todo.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }



}
