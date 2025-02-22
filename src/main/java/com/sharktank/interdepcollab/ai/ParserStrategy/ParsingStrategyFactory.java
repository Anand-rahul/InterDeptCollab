package com.sharktank.interdepcollab.ai.ParserStrategy;

import java.util.Map;

public class ParsingStrategyFactory {
    private final Map<String, JsonParseStrategy> strategies;
    
    public ParsingStrategyFactory(Map<String, JsonParseStrategy> strategies) {
        this.strategies = strategies;
    }
    
    public JsonParseStrategy getStrategy(String format) {
        JsonParseStrategy strategy = strategies.get(format.toLowerCase());
        if (strategy == null) {
            return strategies.get("json"); // Default strategy
        }
        return strategy;
    }
}
