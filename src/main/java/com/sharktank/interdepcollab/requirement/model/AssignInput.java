package com.sharktank.interdepcollab.requirement.model;

import com.sharktank.interdepcollab.devops.model.UserStory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignInput {
    private String user;
    private UserStory userStory;
}
