package com.sharktank.interdepcollab.ai.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.ai.Service.AiCompletionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private AiCompletionService aiCompletionService;

    // @PostMapping("/conversational")
    // public ResponseEntity<ChatResponseDTO> postMethodName(@RequestBody String entity) {
    //     //TODO: process POST request
        
    //     return ;
    // }
    
}
