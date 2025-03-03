package com.sharktank.interdepcollab.ai.ParserStrategy;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SingleJsonParse implements JsonParseStrategy {

	@Override
    public List<Document> parse(String input) throws JsonProcessingException {
        String jsonContent =  new ObjectMapper().writeValueAsString(input);
        return List.of(new Document(jsonContent));
    }
    
}
