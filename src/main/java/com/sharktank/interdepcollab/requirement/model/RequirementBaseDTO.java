package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharktank.interdepcollab.user.model.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequirementBaseDTO {
    private Integer id;
    
    private String title;
    private String description;

    private String requestingDepartment;
    private String subDepartment;
    private String lineOfBusiness;
    private String productName;
    private String priority;

    private Status status;

    private Instant createdDate;
    
    @JsonIgnore
    private AppUser createdBy;

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy.getEmail();
    }

}
