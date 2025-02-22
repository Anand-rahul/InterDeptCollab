package com.sharktank.interdepcollab.ai.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;

@Service
@Slf4j
public class OpenAIEmbeddingService {

    @Value("${spring.azure.ai.embedding.model.url}")
    private String EmbeddingModelUsageEndpoint;

    @Value("${spring.azure.ai.api-key}")
    private String API_KEY; 

    // RestCall
    //public float[] getEmbedding(String text) {
    //     RestTemplate restTemplate = new RestTemplate();
    //     JSONObject requestBody = new JSONObject();
    //     requestBody.put("input", text);
    //     requestBody.put("model", "text-embedding-ada-002");
    //     String response = restTemplate.postForObject(
    //         EmbeddingModelUsageEndpoint,
    //             requestBody.toString(),
    //             String.class
    //     );
    //     JSONObject jsonResponse = new JSONObject(response);
    //     JSONArray embeddingArray = jsonResponse.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");
    //     float[] embedding = new float[embeddingArray.length()];
    //     for (int i = 0; i < embeddingArray.length(); i++) {
    //         embedding[i] = embeddingArray.getFloat(i);
    //     }
    //     return embedding;
    // }
    
    
    public float[] getEmbeddingHttp(String text) throws Exception {
        log.info(EmbeddingModelUsageEndpoint);
        log.info(API_KEY);
        HttpClient client = HttpClient.newHttpClient();

        JSONObject requestBody = new JSONObject();
        requestBody.put("input", text);
        requestBody.put("dimensions", 1536);  

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EmbeddingModelUsageEndpoint))
                .header("Content-Type", "application/json")
                .header("api-key", API_KEY) 
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
        log.info(request.uri().toString());
        log.info(request.headers().firstValue("api-key").toString());
        log.info(requestBody.toString());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());
        log.info("Embedding Json {}",jsonResponse);
        /*
        Expected JSON response structure:
        {
            "object": "list",
            "data": [
                {
                    "object": "embedding",
                    "index": 0,
                    "embedding": [
                        -0.021144172,
                        -0.00938573,
                        -0.008469682,
                        0.051699102,
                        0.0056564664,
                        0.03103548,
                        ... up to 1536 elements ...
                    ]
                }
            ],
            "model": "text-embedding-3-large",
            "usage": {
                "prompt_tokens": 17,
                "total_tokens": 17
            }
        }
        */
        JSONArray embeddingArray = jsonResponse
                .getJSONArray("data")
                .getJSONObject(0)
                .getJSONArray("embedding");

        float[] embedding = new float[embeddingArray.length()];
        for (int i = 0; i < embeddingArray.length(); i++) {
            embedding[i] = embeddingArray.getFloat(i);
        }

        return embedding;
    }
}
