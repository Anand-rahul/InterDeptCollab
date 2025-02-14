package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;

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

    private String createdBy;
    private String deliveryManager;
    private String pmo;

    //TODO: Ensure this can't be updated by the user
    private Integer likeCount;
    private Integer viewCount;

    private Boolean isLiked;

    private Instant createdDate;
    private Instant updatedDate;
}
