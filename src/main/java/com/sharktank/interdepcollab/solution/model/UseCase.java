package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UseCase {
    
    @Id
    @NotNull
    @Column(nullable = false)
    private Integer id;

    private String title;
    private String description;
    private Integer documentId;
    private String dashboardURL;
    private String comments;
    
    @CreationTimestamp
    private Instant createdDate;
    @UpdateTimestamp
    private Instant updatedDate;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    @ToString.Exclude
    private Solution solution;

    @JsonProperty("solutionTitle")
    private String getSolutionTitle(){
        return solution.getTitle();
    }
    
    @JsonProperty("solutionId")
    private Integer getSolutionId(){
        return solution.getId();
    }

}

  