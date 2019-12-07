package com.amigox.todo.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data

@Entity
@Table (name = "labels")

public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int label_id;
    @ManyToMany(mappedBy = "labels")
    private Set<Todo> todos = new HashSet<>();
    String name;
    String slug;
}
