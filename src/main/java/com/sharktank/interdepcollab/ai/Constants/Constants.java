package com.sharktank.interdepcollab.ai.Constants;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {

    @Bean
    public String AiPrePrompt(){
       return """
                You are an AI language model integrated into a Retrieval-Augmented Generation (RAG) system. 
                Your task is to answer user queries strictly within the bounds of the provided context. 
                Under no circumstances should you generate information outside of this context, 
                speculate, or provide content that isn't explicitly supported by the provided materials. 
                If the answer is not available in the context, 
                respond with: 'The information is not available in the provided context.'
                Ensure accuracy, coherence, and relevance in your responses while maintaining context adherence. 
                Begin.";        
            """;
    }
    @Bean
    public Integer EmbeddingPercentageMatch(){
        return 0;
    }
    
}
