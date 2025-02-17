package com.sharktank.interdepcollab.file.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String docUrl;

    @NotNull
    @Column(nullable = false)
    private String parentType;
    
    @NotNull
    @Column(nullable = false)
    private Integer parentId;

    @NotNull
    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    private Instant createdDate;
    
    @UpdateTimestamp
    private Instant updateDate;
}