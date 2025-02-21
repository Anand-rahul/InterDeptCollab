package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.user.model.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionDTO {
    private Integer id;
    private String name;
    private String department;

    @JsonIgnore
    private AppUser createdBy;
    @JsonIgnore
    private AppUser deliveryManager;
    @JsonIgnore
    private AppUser pmo;

    //TODO: Ensure this can't be updated by the user
    private Integer likeCount;
    private Integer viewCount;

    private Boolean isLiked;

    private Instant createdDate;
    private Instant updatedDate;
    
    public String getCreatedBy(){
        return createdBy.getEmail();
    }
     
    public String getDeliveryManager(){
        return createdBy.getEmail();
    }
     
    public String getPMO(){
        return createdBy.getEmail();
    }
}
