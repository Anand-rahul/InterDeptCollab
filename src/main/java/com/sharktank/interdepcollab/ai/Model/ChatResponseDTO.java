package com.sharktank.interdepcollab.ai.Model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO{
    public String guid;
    public Map<String,String> content;
}