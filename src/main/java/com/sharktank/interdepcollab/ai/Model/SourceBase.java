package com.sharktank.interdepcollab.ai.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SourceBase<T> {
    //private SourceType sourceType;
    private String sourceType;
    private Integer id; 
    private T obj;
}
