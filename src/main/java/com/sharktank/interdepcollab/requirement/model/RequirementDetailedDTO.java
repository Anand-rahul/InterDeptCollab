package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.solution.model.SolutionBaseDTO;
import com.sharktank.interdepcollab.user.model.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequirementDetailedDTO {

    private Integer id;
    
    private String title;
    private String description;
    
    private SolutionBaseDTO solution;
    
    private String requestingDepartment;
    private String subDepartment;
    private String lineOfBusiness;
    private String productName;
    private String problemStatement;
    private String expectedImpact;
    private String priority;

    private UserStory userStory;
    @JsonIgnore
    private AppUser assignedTo;
    @JsonIgnore
    private AppUser createdBy;
    
    private Status status;
    
    private Instant createdDate;
    private Instant updatedDate;
    
    private Instant pickedDate;
    private Instant closedDate;

    // BUG: This does not work
    public String getAssignedTo(){
        return assignedTo.getEmail();
    }
    
    public String getCreatedBy(){
        return createdBy.getEmail();
    }
}
