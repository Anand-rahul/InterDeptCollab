package com.sharktank.interdepcollab.ai.Model;

import java.io.InputStream;
import java.util.List;

import com.sharktank.interdepcollab.ai.Constants.SourceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class SourceDocumentBase {
    //private SourceType sourceType;
    private String sourceType;
    private Integer id; 
    private List<InputStream> documents;
}

