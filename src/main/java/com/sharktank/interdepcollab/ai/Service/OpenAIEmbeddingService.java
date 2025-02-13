package com.sharktank.interdepcollab.ai.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;

@Service
public class OpenAIEmbeddingService {

    @Value("${spring.azure.ai.embedding.model.url}")
    private String EmbeddingModelUsageEndpoint;

    @Value("${spring.azure.ai.api-key}")
    private String API_KEY; 

    public float[] getEmbedding(String text) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject requestBody = new JSONObject();
        requestBody.put("input", text);
        requestBody.put("model", "text-embedding-ada-002");

        String response = restTemplate.postForObject(
            EmbeddingModelUsageEndpoint,
                requestBody.toString(),
                String.class
        );

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray embeddingArray = jsonResponse.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");

        float[] embedding = new float[embeddingArray.length()];
        for (int i = 0; i < embeddingArray.length(); i++) {
            embedding[i] = embeddingArray.getFloat(i);
        }

        return embedding;
    }
}
