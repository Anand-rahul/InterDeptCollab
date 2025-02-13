package com.sharktank.interdepcollab.file.model;

import com.sharktank.interdepcollab.solution.model.Solution;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@DiscriminatorValue("SOLUTION")
public class SolutionFile extends File {
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Solution solution;
}
