package com.sharktank.interdepcollab.solution.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String question;    
    @Column(nullable = false, length = 1000)
    private String answer;
    
    @ManyToOne
    @JoinColumn
    @ToString.Exclude
    private Solution solution;
}
