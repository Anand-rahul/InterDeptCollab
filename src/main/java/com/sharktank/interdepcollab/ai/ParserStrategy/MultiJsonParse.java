package com.sharktank.interdepcollab.ai.ParserStrategy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MultiJsonParse implements JsonParseStrategy {

	@Override
	public List<Document> parse(String input) throws JsonProcessingException {
		List<?> items = new ObjectMapper().readValue(input, List.class);
            return items.stream()
                    .map(item -> {
                        try {
                            return new Document(new ObjectMapper().writeValueAsString(item));
                        } catch (JsonProcessingException e) {
                            log.error("Error processing array item", e);
                            return null;
                        }
                    })
                    .filter(doc -> doc != null)
                    .collect(Collectors.toList());
	}
    
}
