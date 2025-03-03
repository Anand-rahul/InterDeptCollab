package com.sharktank.interdepcollab.ai.Model;

import com.sharktank.interdepcollab.ai.Constants.SourceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class SourceBase {
    //private SourceType sourceType;
    private String sourceType;
    private Integer id; 
}
