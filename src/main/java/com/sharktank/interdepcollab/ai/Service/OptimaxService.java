package com.sharktank.interdepcollab.ai.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.text.similarity.CosineSimilarity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharktank.interdepcollab.ai.Model.Optimax;
import com.sharktank.interdepcollab.ai.Repository.OptimaxRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OptimaxService {

    @Autowired
    private OptimaxRepository optimaxRepository;

    @Autowired
    private OpenAIEmbeddingService openAIEmbeddingService;

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public Optimax fetchSimilarityScore(String text1,String text2) throws Exception{
        Optimax op=new Optimax();
        JsonNode node1 = objectMapper.readTree(text1);
        JsonNode node2 = objectMapper.readTree(text2);
        op.setSolId1(Integer.parseInt(node1.get("id").toString()));
        op.setSolId2(Integer.parseInt(node2.get("id").toString()));
        op.setFunctionalSimilarityScore(fetchSimilarityScoreBasedOnFunctional(text1,text2));
        op.setTechnicalSimilarityScore(fetchCosineSimilarityScore(extractInfraDetails(text1), extractInfraDetails(text2)));
        op.setGeneralSimilarityScore(fetchCosineSimilarityScore(text1, text2));
        op.setSimilarityScore((op.getFunctionalSimilarityScore()+op.getGeneralSimilarityScore()+op.getTechnicalSimilarityScore())/3);
        optimaxRepository.save(op);
        return op;
    }
    
    private double fetchSimilarityScoreBasedOnFunctional(String text1, String text2) throws Exception {
        // Convert JSON Strings to JSONObject
        JSONObject obj1 = new JSONObject(text1).optJSONObject("obj");
        JSONObject obj2 = new JSONObject(text2).optJSONObject("obj");

        if (obj1 == null || obj2 == null) {
            log.error("Invalid JSON structure: missing 'obj' key");
            return 0.0;
        }

        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        // Extracting fields
        sb1.append(obj1.optString("detailedExplanantion"))
           .append(" | ")
           .append(obj1.optString("description"))
           .append(" | ")
           .append(obj1.optString("title"));

        sb2.append(obj2.optString("detailedExplanantion"))
           .append(" | ")
           .append(obj2.optString("description"))
           .append(" | ")
           .append(obj2.optString("title"));

        log.info("Processed Functional Texts:\nText1: {}\nText2: {}", sb1, sb2);

        return fetchCosineSimilarityScore(sb1.toString().trim(), sb2.toString().trim());
    }

    private String extractInfraDetails(String json) {
        try {
            JSONObject rootObject = new JSONObject(json);
            JSONArray infraResources = rootObject.optJSONObject("obj").optJSONArray("infraResources");
            
            if (infraResources == null) return "";
            
            return infraResources.toList().stream()
                    .map(obj -> (Map<String, Object>) obj)
                    .map(node -> String.join("|", 
                            String.valueOf(node.getOrDefault("type", "")),
                            String.valueOf(node.getOrDefault("subType", "")),
                            String.valueOf(node.getOrDefault("techStack", ""))))
                    .collect(Collectors.toSet())
                    .toString();
            
        } catch (Exception e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            return "";
        }
    }
    private double calculateCosineSimilarity(String json1, String json2) {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        Map<CharSequence, Integer> vector1 =getFrequencyVector(json1);
        Map<CharSequence, Integer> vector2 = getFrequencyVector(json2);

        return cosineSimilarity.cosineSimilarity(vector1, vector2);
    }

    public double fetchCosineSimilarityScore(float[] vector1, float[] vector2)throws Exception{
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double dotProduct = IntStream.range(0, vector1.length)
                .mapToDouble(i -> vector1[i] * vector2[i])
                .sum();

        double magnitude1 = Math.sqrt(IntStream.range(0, vector1.length)
                .mapToDouble(i -> vector1[i] * vector1[i])
                .sum());

        double magnitude2 = Math.sqrt(IntStream.range(0, vector2.length)
                .mapToDouble(i -> vector2[i] * vector2[i])
                .sum());

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0; 
        }
        return dotProduct;
    }
    private double fetchCosineSimilarityScore(String text1, String text2) throws Exception  {

        float[] vector1= openAIEmbeddingService.getEmbeddingHttp(text1);
        float[] vector2= openAIEmbeddingService.getEmbeddingHttp(text2);
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double dotProduct = IntStream.range(0, vector1.length)
                .mapToDouble(i -> vector1[i] * vector2[i])
                .sum();

        double magnitude1 = Math.sqrt(IntStream.range(0, vector1.length)
                .mapToDouble(i -> vector1[i] * vector1[i])
                .sum());

        double magnitude2 = Math.sqrt(IntStream.range(0, vector2.length)
                .mapToDouble(i -> vector2[i] * vector2[i])
                .sum());

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0; 
        }

        
        return dotProduct;// / (magnitude1 * magnitude2);


        // double sum = 0.0;
        // for (int i = 0; i < vector1.length; i++) {
        //     sum += Math.pow(vector1[i] - vector2[i], 2);
        // }
        
        // return Math.sqrt(sum);


        // double dotProduct = 0.0;
        // double normA = 0.0;
        // double normB = 0.0;

        // for (int i = 0; i < vector1.length; i++) {
        //     dotProduct += vector1[i] * vector2[i];  // A ⋅ B
        //     normA += Math.pow(vector1[i], 2);       // ||A||²
        //     normB += Math.pow(vector1[i], 2);       // ||B||²
        // }

        // normA = Math.sqrt(normA);  // ||A||
        // normB = Math.sqrt(normB);  // ||B||

        // return (normA == 0 || normB == 0) ? 0.0 : dotProduct / (normA * normB);
    }

    private static String jsonToNormalizedText(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            StringBuilder sb = new StringBuilder();
            extractJsonFields(node, sb, "");
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON", e);
        }
    }
    private static void extractJsonFields(JsonNode node, StringBuilder sb, String prefix) {
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String field = fieldNames.next();
                extractJsonFields(node.get(field), sb, prefix + field + ":");
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                extractJsonFields(arrayElement, sb, prefix);
            }
        } else {
            sb.append(prefix).append(node.asText()).append(" ");
        }
    }

    private static Map<CharSequence, Integer> getFrequencyVector(String text) {
        Map<CharSequence, Integer> vector = new HashMap<>();
        for (String word : text.split("\\s+")) {
            vector.put(word, vector.getOrDefault(word, 0) + 1);
        }
        return vector;
    }
    
}
