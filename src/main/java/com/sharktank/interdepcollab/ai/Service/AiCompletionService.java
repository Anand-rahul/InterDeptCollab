package com.sharktank.interdepcollab.ai.Service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sharktank.interdepcollab.ai.Constants.Constants;
import com.sharktank.interdepcollab.ai.DTO.ChatResponseDTO;
import com.sharktank.interdepcollab.ai.DTO.VectorFetchDTO;
import com.sharktank.interdepcollab.ai.Model.ChatSession;
import com.sharktank.interdepcollab.ai.Model.Message;
import com.sharktank.interdepcollab.ai.Repository.ChatSessionRepository;
import com.sharktank.interdepcollab.ai.Repository.MessageRepository;
import com.sharktank.interdepcollab.ai.Repository.VectorRepository;
import com.sharktank.interdepcollab.solution.repository.SolutionRepository;

import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class AiCompletionService {
    
    @Autowired
    private ChatSessionRepository chatSessionRepository;
    @Autowired
    private Constants constants;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private VectorRepository vectorRepository;
    @Autowired
    private DataLoader dataLoaderService;

    @Autowired
    private OpenAIEmbeddingService openAIEmbeddingService;

    @Autowired
    private OptimaxService optimaxService;

    @Autowired
    private SolutionRepository solutionRepository;

    @Value("${spring.azure.ai.chat.model.url}")
    private String conversationalAiEndpoint;

    @Value("${spring.azure.ai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // public ChatResponseDTO getContextualChat( String chatGuidStr, String prompt) {
    //     UUID chatGuid = UUID.fromString(chatGuidStr);
    //     // Find or create chat session
    //     ChatSession chatSession = chatSessionRepository.findByGuid(chatGuid)
    //             .orElseGet(() -> {
    //                 ChatSession newSession = new ChatSession();
    //                 newSession.setGuid(chatGuid);
    //                 newSession.setTitle("New Chat Session");
    //                 newSession.setCreatedAt(Instant.now());
    //                 newSession.setUpdatedAt(Instant.now());
    //                 return chatSessionRepository.save(newSession);
    //             });
    //     ChatResponseDTO promptResponse=new ChatResponseDTO();
    //     promptResponse.guid=chatSession.getGuid().toString();
    //     List<Map<String,String>> newChatContext=new ArrayList();
    //     List<Message> chatMessages = messageRepository.findByChatGuidOrderByTimestamp(chatGuid);
    //     List<Map<String, Object>> messages = new ArrayList<>();
    //     messages.add(Map.of(
    //         "role", "system",
    //         "content", List.of(Map.of("type", "text", "text", "You are an AI assistant that helps people find information."))
    //     ));
    //     newChatContext.add(Map.of("system","You are an AI assistant that helps people find information."));
    //     for (Message message : chatMessages) {
    //         messages.add(Map.of(
    //             "role", message.getMessageType(),
    //             "content", List.of(Map.of("type", "text", "text", message.getMessageText()))
    //         ));
    //         newChatContext.add(Map.of(message.getMessageType(),message.getMessageText()));
    //     }  
    //     messages.add(Map.of(
    //         "role", "user",
    //         "content", List.of(Map.of("type", "text", "text", prompt))
    //     ));
    //     newChatContext.add(Map.of("user",prompt));
    //     Map<String, Object> requestBody = Map.of(
    //         "messages", messages,
    //         "temperature", 0.7
    //     );
    //     String response = sendRequest(requestBody);
    //     newChatContext.add(Map.of("assistant",response));
    //     promptResponse.content=newChatContext;
    //     Message userMessage = new Message();
    //     userMessage.setChatGuid(chatGuid);
    //     userMessage.setMessageType("user");
    //     userMessage.setMessageText(prompt);
    //     userMessage.setTimestamp(Instant.now());
    //     Message aiMessage = new Message();
    //     aiMessage.setChatGuid(chatGuid);
    //     aiMessage.setMessageType("assistant");
    //     aiMessage.setMessageText(response);
    //     aiMessage.setTimestamp(Instant.now());
    //     messageRepository.save(userMessage);
    //     messageRepository.save(aiMessage);
    //     chatSession.setUpdatedAt(Instant.now());
    //     chatSessionRepository.save(chatSession);
    //     return promptResponse;
    // }

    public Set<String> fetchSolutionsBySimilarName(String paramToBeSearched)throws Exception{
        Set<String> sourceIdSet=new HashSet<>();
        var ids=solutionRepository.getAllSolutionsLike(paramToBeSearched);
        for(String id:ids){
            sourceIdSet.add(id);
        }
        return sourceIdSet;
    }

    public Set<String> fetchSimilarSolutions(String query,Integer k) throws Exception{
        float[] queryEmbedding = openAIEmbeddingService.getEmbeddingHttp(query);
        String embeddingVector=dataLoaderService.getVectorString(queryEmbedding);
        List<Object[]> results = vectorRepository.searchByCosineSimilarity("solution".toUpperCase()
                    , embeddingVector
                    ,k);
        log.info("fetched :"+results.size());
        Set<String> sourceIds=new HashSet<>();
        for (Object[] row : results) {
            
            float[] embeddingArray = null;
            Object embeddingObj = row[3];
            
            if (embeddingObj != null) {
                if (embeddingObj instanceof float[]) {
                    embeddingArray = (float[]) embeddingObj;
                } else if (embeddingObj instanceof String) {
                    embeddingArray = convertVectorStringToFloatArray((String) embeddingObj);
                } else {
                    embeddingArray = convertVectorStringToFloatArray(embeddingObj.toString());
                }
            }
            
            if (embeddingArray == null) {
                log.warn("Could not parse embedding for row: " + Arrays.toString(row));
                continue;
            }
            
            // Create DTO with the properly parsed embedding
            VectorFetchDTO vecFetch = new VectorFetchDTO(
                (String) row[0],
                (String) row[1], 
                (String) row[2],
                embeddingArray
            );
            
            double opScore = optimaxService.fetchCosineSimilarityScore(queryEmbedding, vecFetch.embeds) * 100;
            log.info("Calculated opScore: {}", opScore);
            
            // Check if similarity is greater than threshold
            if (opScore >= constants.EmbeddingPercentageMatch()) {
                JSONObject jsonObject = new JSONObject(vecFetch.getJsonData());
                log.info("fetched json :" + jsonObject);
                String sourceId = jsonObject.optString("sourceId", "");
                sourceIds.add(sourceId);
            }
        }
        return sourceIds;
    }
    private float[] convertVectorStringToFloatArray(String vectorString) {
        if (vectorString == null || vectorString.isEmpty()) {
            return new float[0]; 
        }
        try {
            String cleanedString = vectorString
                .replaceAll("[\\[\\]{}]", "") 
                .replaceAll(",$", "")         
                .trim();
            if (cleanedString.isEmpty()) {
                return new float[0];
            }
            
            String[] elements = cleanedString.split(",");
            float[] result = new float[elements.length];
            for (int i = 0; i < elements.length; i++) {
                result[i] = Float.parseFloat(elements[i].trim());
            }
            
            return result;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing vector string: " , e);
        }
    }
    public String fetchTopKMatches(String query,String id, Integer k) throws Exception {
        float[] embedding = openAIEmbeddingService.getEmbeddingHttp(query);        
        log.info(""+id.getClass()+" "+id);
        List<Object[]> results = vectorRepository.searchSolutionByCosineSimilarity("SOLUTION_DOCUMENT"
                    , dataLoaderService.getVectorString(embedding)
                    ,k,id);

        List<VectorFetchDTO> vectors = results.stream()
            .map(row -> new VectorFetchDTO((String) row[0], (String) row[1], (String) row[2]))
            .toList();

        log.info("fetched "+vectors.size()+" records");
        StringBuilder textChunks=new StringBuilder();
        if(vectors.size()==0){
            return "";
        }
        for(var vector:vectors){
            textChunks.append(vector.text);
            textChunks.append("\n");
        }
        return textChunks.toString();
    }

    public ChatResponseDTO getContextualChatOptimized(String chatGuidStr, String prompt,String solutionId)throws Exception {

        StringBuilder enrichedPrompt=new StringBuilder();
        String context = fetchTopKMatches(prompt,solutionId,50);
        enrichedPrompt.append("Context: ").append(context).append("    ");
        enrichedPrompt.append("prompt: ").append(prompt);
        UUID chatGuid = UUID.fromString(chatGuidStr);
        ChatSession chatSession = chatSessionRepository.findByGuid(chatGuid)
                .orElseGet(() -> chatSessionRepository.save(new ChatSession(chatGuid, "New Chat Session",Instant.now(),Instant.now())));
    
        List<Message> chatMessages = messageRepository.findByChatGuidOrderByTimestamp(chatGuid);
        List<Map<String, Object>> messages = new ArrayList<>();
    
        messages.add(Map.of(
                "role", "system",
                "content", List.of(Map.of("type", "text", "text", constants.AiPrePrompt()))
        ));
    
        List<Map<String, String>> newChatContext = new ArrayList<>();
        newChatContext.add(Map.of("system", constants.AiPrePrompt()));
    
        chatMessages.forEach(message -> {
            messages.add(Map.of(
                    "role", message.getMessageType(),
                    "content", List.of(Map.of("type", "text", "text", message.getMessageText()))
            ));
            newChatContext.add(Map.of(message.getMessageType(), message.getMessageText()));
        });
        
        messages.add(Map.of(
                "role", "user",
                "content", List.of(Map.of("type", "text", "text", enrichedPrompt.toString()))
        ));
        newChatContext.add(Map.of("user", enrichedPrompt.toString()));
    
        String response = sendRequest(Map.of("messages", messages, "temperature", 0.7));
        newChatContext.add(Map.of("assistant", response));
    
        List<Message> newMessages = new ArrayList<>();
        newMessages.add(new Message(chatGuid, "user", enrichedPrompt.toString(),Instant.now()));
        newMessages.add(new Message(chatGuid, "assistant", response,Instant.now()));

        messageRepository.saveAll(newMessages);
    
        chatSession.setUpdatedAt(Instant.now());
        chatSessionRepository.save(chatSession);
    
        return new ChatResponseDTO(chatSession.getGuid().toString(), newChatContext);
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
