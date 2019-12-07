package com.amigox.todo.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data

@Entity
@Table (name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int todo_id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    User user;
    String title;
    String note;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "Todo_Label",
            joinColumns = { @JoinColumn(name = "todo_id") },
            inverseJoinColumns = { @JoinColumn(name = "label_id") }
    )
    Set<Label> labels = new HashSet<>();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    Date updatedAt;

    int priority;

    @PrePersist
    protected void onCreate() {
        updatedAt = createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public Todo() {

    }
}
