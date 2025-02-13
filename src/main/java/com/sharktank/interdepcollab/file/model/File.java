package com.sharktank.interdepcollab.file.model;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Inheritance
@DiscriminatorColumn(name = "FILE_TYPE")
@Table(name = "file_metadata")
public abstract class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String location;

    @NotNull
    private Instant uploadDate;
}