package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;
import java.util.Set;

import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.file.model.*;
import com.sharktank.interdepcollab.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    private String title;
    private String description;

    @OneToOne(mappedBy = "requirement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserStory userStory;
    
    @ManyToOne
    @JoinColumn
    private User assignedTo;
    
    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false)
    private User createdBy;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    private Instant createdDate;
    private Instant pickedDate;
    private Instant closedDate;

    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RequirementFile> files;
}
