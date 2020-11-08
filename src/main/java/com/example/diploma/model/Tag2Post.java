package com.example.diploma.model;

import javax.persistence.*;

@Entity
@Table(name = "tag2post")
public class Tag2Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
