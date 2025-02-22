package com.sharktank.interdepcollab.ai.Model;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

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
