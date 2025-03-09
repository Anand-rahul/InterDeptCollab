package com.sharktank.interdepcollab.ai.DTO;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO{
    public String guid;
    public List<Map<String,String>> content;
}