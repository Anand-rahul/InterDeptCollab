package com.sharktank.interdepcollab.file.model;

import com.sharktank.interdepcollab.requirement.model.Requirement;

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
@DiscriminatorValue("REQUIREMENT")
public class RequirementFile extends File {
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Requirement requirement;
}
