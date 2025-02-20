package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;

import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.file.model.*;
import com.sharktank.interdepcollab.user.model.AppUser;

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
    private AppUser assignedTo;
    
    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false)
    @CreatedBy
    private AppUser createdBy;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @CreationTimestamp
    private Instant createdDate;
    @UpdateTimestamp
    private Instant updatedDate;

    private Instant pickedDate;
    private Instant closedDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FileMetadata> files;
}
