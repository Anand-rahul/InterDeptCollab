package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;
import java.util.Set;

import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.file.model.SolutionFile;
import com.sharktank.interdepcollab.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    private String name;
    private String department;
    
    @ManyToOne
    @JoinColumn
    private User createdBy;
    
    @ManyToOne
    @JoinColumn
    private User deliveryManager;
    
    @ManyToOne
    @JoinColumn
    private User pmo;
    
    private Instant createdDate;
    private Instant updatedDate;
    
    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FAQ> faqs;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SolutionFile> files;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserStory> userStories;
}

