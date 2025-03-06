package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.file.model.*;
import com.sharktank.interdepcollab.solution.model.Solution;
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

    // Business Related fields
    private String requestingDepartment;
    private String subDepartment;
    private String lineOfBusiness;
    private String productName;

    @ManyToOne
    @JoinColumn
    private Solution solution;

    @NotNull
    private String title;
    private String description;
    private String problemStatement;
    private String expectedImpact;
    private String priority;

    @ManyToOne
    @JoinColumn
    private AppUser assignedTo;

    @OneToOne(mappedBy = "requirement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserStory userStory;

    @ManyToOne
    @JoinColumn(nullable = false)
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
