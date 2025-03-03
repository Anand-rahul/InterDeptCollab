package com.sharktank.interdepcollab.ai.Model;

import jakarta.persistence.*;
import lombok.ToString;

import java.util.UUID;


@Entity
@Table(name = "vectors")
@ToString
public class VectorStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String sourceType; 

    @Column(nullable = false, unique = true)
    public UUID sourceId; 

    @Column(columnDefinition = "TEXT")
    public String text;

    @Column(columnDefinition = "TEXT")
    public String jsonData; 

    @Column(columnDefinition = "vector(1536)", nullable = false)
    public float[] embedding;

    public VectorStore() {}

    public VectorStore(String sourceType, UUID sourceId, String text, String jsonData, float[] embedding) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.text = text;
        this.jsonData = jsonData;
        this.embedding = embedding;
    }
}

