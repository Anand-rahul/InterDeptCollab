package com.sharktank.interdepcollab.ai.Controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.sharktank.interdepcollab.ai.Constants.SourceType;
import com.sharktank.interdepcollab.ai.DTO.TopKFetchDTO;
import com.sharktank.interdepcollab.ai.DTO.VectorizeDTO;
import com.sharktank.interdepcollab.ai.Service.AiCompletionService;
import com.sharktank.interdepcollab.ai.Service.DataLoader;
import com.sharktank.interdepcollab.solution.model.Solution;
import com.sharktank.interdepcollab.solution.model.SolutionBaseDTO;
import com.sharktank.interdepcollab.solution.service.SolutionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/vectorize")
@Slf4j
public class VectorController  {
    
    @Autowired
    public DataLoader dataLoaderService;

    @Autowired
    public AiCompletionService aiCompletionService;

    @Autowired 
    public SolutionService solutionService;


    @PostMapping("/json")
    public ResponseEntity<List<String>> createEmbeddings(@RequestBody VectorizeDTO json) throws Exception {
        log.info(json.toString());
        List<String> result = dataLoaderService.vectorizeObjectStrategy(json.jsonObj,json.sourceType,json.format);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/solJson")
    public  ResponseEntity<List<String>> createEmbed(@RequestBody JsonNode entity) throws Exception {
        List<String> generatedUUIDs=new ArrayList<>();
        String sourceType = entity.get("sourceType").asText();
        Integer id = entity.get("id").asInt();
        log.info("Received Enity with sourceType:"+sourceType+" id:"+id);
        if(sourceType.equalsIgnoreCase("SOLUTION")){
            generatedUUIDs=dataLoaderService.vectorizeObject(entity, SourceType.SOLUTION, id.toString());
        }
            //log.info("here");
            
        return ResponseEntity.ok(generatedUUIDs);
    }
    
    @PostMapping("/fetchTopK")
    public ResponseEntity<List<SolutionBaseDTO>> fetchTopKMatches(@RequestBody TopKFetchDTO entity) throws Exception {
        // First fetch solutions by similar name
        Set<String> idStrings = aiCompletionService.fetchSolutionsBySimilarName(entity.getQuery());
        
        // Then fetch similar solutions and combine the results
        Set<String> similarSolutionIds = aiCompletionService.fetchSimilarSolutions(entity.getQuery(), entity.getK());
        idStrings.addAll(similarSolutionIds);
        
        // Handle potential empty set
        if (idStrings.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        int[] ids;
        try {
            ids = idStrings.stream().mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            throw new Exception("Invalid ID format in the results", e);
        }
        
        List<SolutionBaseDTO> listOfSimilarSolutions = solutionService.getAllSolutions(ids);
        return ResponseEntity.ok(listOfSimilarSolutions);
    }
    
}
