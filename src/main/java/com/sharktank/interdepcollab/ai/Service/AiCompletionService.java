package com.sharktank.interdepcollab.ai.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sharktank.interdepcollab.ai.Model.ChatResponseDTO;
import com.sharktank.interdepcollab.ai.Model.ChatSession;
import com.sharktank.interdepcollab.ai.Model.Message;
import com.sharktank.interdepcollab.ai.Repository.ChatSessionRepository;
import com.sharktank.interdepcollab.ai.Repository.MessageRepository;

import java.time.Instant;
import java.util.*;

@Service
public class AiCompletionService {
    
    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Value("${spring.azure.ai.chat.model.url}")
    private String conversationalAiEndpoint;

    @Value("${spring.azure.ai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponseDTO getContextualChat( String chatGuidStr, String prompt) {
        UUID chatGuid = UUID.fromString(chatGuidStr);

        // Find or create chat session
        ChatSession chatSession = chatSessionRepository.findByGuid(chatGuid)
                .orElseGet(() -> {
                    ChatSession newSession = new ChatSession();
                    newSession.setGuid(chatGuid);
                    newSession.setTitle("New Chat Session");
                    newSession.setCreatedAt(Instant.now());
                    newSession.setUpdatedAt(Instant.now());
                    return chatSessionRepository.save(newSession);
                });
        ChatResponseDTO promptResponse=new ChatResponseDTO();
        promptResponse.guid=chatSession.getGuid().toString();
        List<Map<String,String>> newChatContext=new ArrayList();
        List<Message> chatMessages = messageRepository.findByChatGuidOrderByTimestamp(chatGuid);
        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(Map.of(
            "role", "system",
            "content", List.of(Map.of("type", "text", "text", "You are an AI assistant that helps people find information."))
        ));
        newChatContext.add(Map.of("system","You are an AI assistant that helps people find information."));
        for (Message message : chatMessages) {
            messages.add(Map.of(
                "role", message.getMessageType(),
                "content", List.of(Map.of("type", "text", "text", message.getMessageText()))
            ));
            newChatContext.add(Map.of(message.getMessageType(),message.getMessageText()));
        }
        
        messages.add(Map.of(
            "role", "user",
            "content", List.of(Map.of("type", "text", "text", prompt))
        ));
        newChatContext.add(Map.of("user",prompt));
        Map<String, Object> requestBody = Map.of(
            "messages", messages,
            "temperature", 0.7
        );

        String response = sendRequest(requestBody);
        newChatContext.add(Map.of("assistant",response));
        promptResponse.content=newChatContext;
        Message userMessage = new Message();
        userMessage.setChatGuid(chatGuid);
        userMessage.setMessageType("user");
        userMessage.setMessageText(prompt);
        userMessage.setTimestamp(Instant.now());

        Message aiMessage = new Message();
        aiMessage.setChatGuid(chatGuid);
        aiMessage.setMessageType("assistant");
        aiMessage.setMessageText(response);
        aiMessage.setTimestamp(Instant.now());

        messageRepository.save(userMessage);
        messageRepository.save(aiMessage);
        chatSession.setUpdatedAt(Instant.now());
        chatSessionRepository.save(chatSession);

        return promptResponse;
    }
    private String sendRequest(Map<String, Object> requestBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    conversationalAiEndpoint, HttpMethod.POST, requestEntity, Map.class
            );

            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody == null) {
                return "No response from AI.";
            }

            List<?> choices = (List<?>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "No AI response.";
            }

            Map<?, ?> choice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) choice.get("message");
            if (message == null) {
                return "No response content.";
            }
            
            return (String) message.get("content");
        } catch (Exception e) {
            return "Error generating response: " + e.getMessage();
        }
    }
}
