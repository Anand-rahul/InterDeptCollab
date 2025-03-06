package com.sharktank.interdepcollab.ai.Model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class VectorFetchDTO {
    public String text;
    public String jsonData;
    public String sourceId;

    public VectorFetchDTO(String text,String jsonData,String sourceId){
        this.text=text;
        this.jsonData=jsonData;
        this.sourceId=sourceId;
    }

    
}
