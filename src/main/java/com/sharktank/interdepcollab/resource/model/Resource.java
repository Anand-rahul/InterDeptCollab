package com.sharktank.interdepcollab.resource.model;

import java.time.Instant;

import com.sharktank.interdepcollab.file.model.File;
import com.sharktank.interdepcollab.user.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    private String name;
    
    @NotNull
    @OneToOne(mappedBy = "resources", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private File file;
    
    @NotNull
    private User createdBy;

    private Instant uploadDate;
}
