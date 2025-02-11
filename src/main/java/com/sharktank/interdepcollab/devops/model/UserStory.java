package com.sharktank.interdepcollab.devops.model;

import com.sharktank.interdepcollab.requirement.model.Requirement;
import com.sharktank.interdepcollab.solution.model.Solution;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String project;
    
    @OneToOne
    @JoinColumn
    private Requirement requirements;
    
    @ManyToOne
    @JoinColumn
    private Solution solutions;
}
