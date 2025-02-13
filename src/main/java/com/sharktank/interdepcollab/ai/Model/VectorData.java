package com.sharktank.interdepcollab.ai.Model;

import jakarta.persistence.*;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name = "vectors")
public class VectorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceType; 

    @Column(nullable = false, unique = true)
    private UUID sourceId; 

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "JSONB")
    private JsonNode jsonData; 

    @Column(columnDefinition = "vector(1536)", nullable = false)
    private float[] embedding;

    public VectorData() {}

    public VectorData(String sourceType, UUID sourceId, String text, JsonNode jsonData, float[] embedding) {
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.text = text;
        this.jsonData = jsonData;
        this.embedding = embedding;
    }
}

