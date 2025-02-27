package com.sharktank.interdepcollab.requirement.model;

import java.time.Instant;

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

}
