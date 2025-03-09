package com.sharktank.interdepcollab.ai.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VectorizeDTO {
    public String sourceType;
    public String jsonObj;
    public String format;
}
