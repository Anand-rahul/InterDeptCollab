package com.sharktank.interdepcollab.file.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String location;

    // Generic foreign key relationship
    @ManyToOne
    @JoinColumn(nullable = false)
    private Object parent;  // This will be either Requirement or Solution

    @Column(nullable = false)
    private String parentType;  // Store the type of parent ("REQUIREMENT" or "SOLUTION")
        
    public void setParent(Object parent) {
        this.parent = parent;
        this.parentType = parent.getClass().getSimpleName().toUpperCase();
    }
}