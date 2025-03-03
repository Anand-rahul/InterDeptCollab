package com.sharktank.interdepcollab.ai.ParserStrategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParsingStrategyConfig {
    
    @Bean
    public ParsingStrategyFactory parsingStrategyFactory() {
        Map<String, JsonParseStrategy> strategies = new HashMap<>();
        strategies.put("multi-json", new MultiJsonParse());
        strategies.put("json", new SingleJsonParse());
        return new ParsingStrategyFactory(strategies);
    }
}