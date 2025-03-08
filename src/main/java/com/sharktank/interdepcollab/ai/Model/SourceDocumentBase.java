package com.sharktank.interdepcollab.ai.Model;

import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SourceDocumentBase {
    //private SourceType sourceType;
    private String sourceType;
    private Integer id; 
    private InputStream documents;
    private String fileName;
}

