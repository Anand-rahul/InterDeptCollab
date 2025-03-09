package com.sharktank.interdepcollab.ai.DTO;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class VectorFetchDTO {
    public String text;
    public String jsonData;
    public String sourceId;
    public float[] embeds;

    public VectorFetchDTO(String text,String jsonData,String sourceId,float[] embeds){
        this.text=text;
        this.jsonData=jsonData;
        this.sourceId=sourceId;
        this.embeds=embeds;
    }
    public VectorFetchDTO(String text,String jsonData,String sourceId){
        this.text=text;
        this.jsonData=jsonData;
        this.sourceId=sourceId;
    }

    
}
