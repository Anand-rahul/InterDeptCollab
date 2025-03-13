package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.solution.model.SolutionBaseDTO;
import com.sharktank.interdepcollab.user.model.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequirementDetailedDTO extends RequirementBaseDTO{
    
    @JsonIgnore
    private SolutionBaseDTO solution;
        
    private String problemStatement;
    private String expectedImpact;

    private UserStory userStory;
    @JsonIgnore
    private AppUser assignedTo;
    
    private Instant updatedDate;
    
    private Instant pickedDate;
    private Instant closedDate;

    @JsonProperty("assignedTo")
    public String getAssignedTo(){
        return assignedTo != null ? assignedTo.getEmail() : null;
    }
    
    @JsonProperty("solutionId")
    public Integer getSolutionId(){
        return solution != null ? solution.getId() : null;
    }
}
