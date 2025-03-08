package com.sharktank.interdepcollab.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.solution.model.Solution;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("SOLUTION")
public class SolutionAction extends Action {
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Solution solution;
}
