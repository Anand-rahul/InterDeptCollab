package com.sharktank.interdepcollab.ai.ParserStrategy;

import java.util.List;

import org.springframework.ai.document.Document;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonParseStrategy {
    List<Document> parse(String input) throws JsonProcessingException;
}
