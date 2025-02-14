package com.sharktank.interdepcollab.resource.model;

import java.time.Instant;

import com.sharktank.interdepcollab.file.model.*;
import com.sharktank.interdepcollab.user.model.AppUser;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    private String name;
    
    @NotNull
    @OneToOne(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ResourceFile file;
    
    @NotNull
    private AppUser createdBy;

    private Instant uploadDate;
}
