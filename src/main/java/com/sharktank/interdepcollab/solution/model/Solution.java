package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.user.model.AppUser;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String problemStatement;
    private String impact;
    private String category;
    private String tags;
    private String department;
    private String detailedExplanantion;

    private Integer preReqDocumentId;
    private Status status;
    
    @NotNull
    @Column(nullable = false)
    private String title;
    private String description;
    
    @ManyToOne
    @JoinColumn
    private AppUser createdBy;
    
    @ManyToOne
    @JoinColumn
    private AppUser deliveryManager;
    
    @ManyToOne
    @JoinColumn
    private AppUser pmo;

    private int likeCount;
    private int viewCount;
    
    @CreationTimestamp
    private Instant createdDate;
    @UpdateTimestamp
    private Instant updatedDate;
    
    // TODO: Add Infra Resource for solutions
    
    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InfraResource> infraResources; 

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FAQ> faqs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FileMetadata> files;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserStory> userStories;
    
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UseCase> useCases;

    public void addView() {
        this.viewCount++;
    }

    public void addLike() {
        this.likeCount++;
    }

    public void removeLike() {
        this.likeCount--;
    }
}

