package com.sharktank.interdepcollab.requirement.model;

import java.util.List;

import com.sharktank.interdepcollab.solution.model.Solution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequirementInput {

    private String title;
    private String description;
    private List<Integer> files;
    private Solution solution;
    
    private String requestingDepartment;
    private String subDepartment;
    private String lineOfBusiness;
    private String productName;
    private String problemStatement;
    private String expectedImpact;
    private String priority;
}
