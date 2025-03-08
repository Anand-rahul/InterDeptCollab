package com.sharktank.interdepcollab.solution.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionInput {
    private String title;
    private String description;

    private String problemStatement;
    private String impact;
    private String category;
    private String tags;
    private String department;
    private String detailedExplanantion;

    private String deliveryManager;
    private String pmo;

    private List<InfraResource> infraResources;
    private List<Integer> files;
}
