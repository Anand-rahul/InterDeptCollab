package com.sharktank.interdepcollab.requirement.model;

import java.util.List;

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
}
