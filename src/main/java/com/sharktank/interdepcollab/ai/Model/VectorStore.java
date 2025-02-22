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
    private Long id;

    @Column(nullable = false)
    private String sourceType; 

    @Column(nullable = false, unique = true)
    private UUID sourceId; 

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String jsonData; 

    @Column(columnDefinition = "vector(1536)", nullable = false)
    private float[] embedding;

    public VectorStore() {}

    public VectorStore(String sourceType, UUID sourceId, String text, String jsonData, float[] embedding) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.text = text;
        this.jsonData = jsonData;
        this.embedding = embedding;
    }
}

