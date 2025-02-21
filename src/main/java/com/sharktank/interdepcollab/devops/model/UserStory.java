package com.sharktank.interdepcollab.devops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.requirement.model.Requirement;
import com.sharktank.interdepcollab.solution.model.Solution;

import groovy.transform.builder.Builder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String project;
    
    @OneToOne
    @JoinColumn
    @JsonIgnore
    private Requirement requirement;
    
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Solution solution;
}
