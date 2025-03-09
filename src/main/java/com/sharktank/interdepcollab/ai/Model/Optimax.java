package com.sharktank.interdepcollab.ai.Model;

import com.azure.core.annotation.Generated;

import groovy.transform.ToString;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "optimax")
@ToString
public class Optimax {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Integer solId1;

    @Column(nullable = false)
    public Integer solId2;

    @Column(nullable = false)
    public double similarityScore;

    @Column(nullable = false)
    public double technicalSimilarityScore;

    @Column(nullable = false)
    public double functionalSimilarityScore;

    @Column(nullable = false)
    public double generalSimilarityScore;

    public String comments;
    
    public ActionType actionType;

}
