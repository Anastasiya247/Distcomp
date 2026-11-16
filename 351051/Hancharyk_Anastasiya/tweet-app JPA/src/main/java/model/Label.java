package com.example.tweetapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_label")
public class Label extends BaseEntity {
    @Column(nullable = false, unique = true, length = 32)
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}