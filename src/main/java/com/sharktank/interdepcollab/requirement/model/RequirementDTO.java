package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.devops.model.UserStory;
import com.sharktank.interdepcollab.user.model.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequirementDTO {

    private Integer id;
    private String title;
    private String description;

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

    public String getAssignedTo(){
        return assignedTo.getEmail();
    }
    
    public String getCreatedBy(){
        return createdBy.getEmail();
    }
}
