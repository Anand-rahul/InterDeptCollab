package com.sharktank.interdepcollab.ai.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.ai.Model.ChatRequestDTO;
import com.sharktank.interdepcollab.ai.Model.ChatResponseDTO;
import com.sharktank.interdepcollab.ai.Model.VectorFetchDTO;
import com.sharktank.interdepcollab.ai.Repository.VectorRepository;
import com.sharktank.interdepcollab.ai.Service.AiCompletionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    @Autowired
    private AiCompletionService aiCompletionService;

    @PostMapping("/conversational")
    public ResponseEntity<ChatResponseDTO> ContenxtualLLMChat(@RequestBody ChatRequestDTO input) throws Exception{
        if(input.guid==null || input.guid==""){
            input.guid=UUID.randomUUID().toString();
        }
        ChatResponseDTO chatResponseDTO=aiCompletionService.getContextualChatOptimized(input.guid, input.query);


        return ResponseEntity.ok(chatResponseDTO);
    }
}
