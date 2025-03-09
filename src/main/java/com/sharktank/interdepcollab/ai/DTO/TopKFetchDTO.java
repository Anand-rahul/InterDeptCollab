package com.sharktank.interdepcollab.ai.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopKFetchDTO {
    public String query;
    public Integer k;
}
