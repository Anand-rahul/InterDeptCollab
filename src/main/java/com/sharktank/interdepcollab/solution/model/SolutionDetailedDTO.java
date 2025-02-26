package com.sharktank.interdepcollab.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharktank.interdepcollab.user.model.AppUser;

public class SolutionDetailedDTO extends SolutionBaseDTO {
    @JsonIgnore
    private AppUser createdBy;
    @JsonIgnore
    private AppUser deliveryManager;
    @JsonIgnore
    private AppUser pmo;
    
    private String problemStatement;
    private String impact;
    private String category;
    private String tags;

    // BUG: This is not returning anything
    public String getCreatedBy() {
        return createdBy.getEmail();
    }

    public String getDeliveryManager() {
        return createdBy.getEmail();
    }

    public String getPMO() {
        return createdBy.getEmail();
    }
}
