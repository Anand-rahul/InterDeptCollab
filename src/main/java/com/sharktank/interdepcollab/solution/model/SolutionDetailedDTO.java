package com.sharktank.interdepcollab.solution.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sharktank.interdepcollab.user.model.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
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
    
    private int preReqDocumentId;
    private Status status;

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy != null ? createdBy.getEmail() : null;
    }

    @JsonProperty("deliveryManager")
    public String getDeliveryManager() {
        return deliveryManager != null ? deliveryManager.getEmail() : null;
    }

    @JsonProperty("pmo")
    public String getPMO() {
        return pmo != null ? pmo.getEmail() : null;
    }
}
