package com.sharktank.interdepcollab.solution.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class InfraResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonBackReference
    private Integer id;
    private String name;
    private String type;
    private String subType;
    private String techStack;
    private String budgetCode;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn
    @ToString.Exclude
    @JsonIgnore
    private Solution solution;
}
