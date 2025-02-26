package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.user.model.AppUser;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Integer id;
    
    private String problemStatement;
    private String impact;
    private String category;
    private String tags;
    private String department;
    
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
    
    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FAQ> faqs;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FileMetadata> files;

    @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserStory> userStories;

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

