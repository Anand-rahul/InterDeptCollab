package com.sharktank.interdepcollab.ai.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharktank.interdepcollab.ai.Model.VectorizeDTO;
import com.sharktank.interdepcollab.ai.Service.DataLoader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@RestController
@RequestMapping("/v1/vectorize")
@Slf4j
public class VectorController  {
    
    @Autowired
    public DataLoader dataLoaderService;

    @PostMapping("/single/json")
    public ResponseEntity<List<String>> createEmbeddings(@RequestBody VectorizeDTO json) throws Exception {
        log.info(json.toString());
        List<String> result = dataLoaderService.vectorizeObjectStrategy(json.jsonObj,json.sourceType,json.format);
        return ResponseEntity.ok(result);
    }
}
