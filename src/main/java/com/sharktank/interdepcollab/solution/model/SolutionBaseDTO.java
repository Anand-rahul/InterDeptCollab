package com.sharktank.interdepcollab.solution.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionBaseDTO {
    private Integer id;
    private String title;
    private String description;
    private String department;

    //TODO: Ensure this can't be updated by the user
    private Integer likeCount;
    private Integer viewCount;

    private Boolean isLiked;

    private Instant createdDate;
    private Instant updatedDate;

}
