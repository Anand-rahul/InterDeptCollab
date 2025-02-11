package com.sharktank.interdepcollab.ai.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class AiCompletionService {

    @Autowired
    private WebClient webClient;
    
}