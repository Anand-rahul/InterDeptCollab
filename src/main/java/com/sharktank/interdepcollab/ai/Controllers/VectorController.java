package com.sharktank.interdepcollab.ai.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.ai.Service.DataLoader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/vectorize")
@RequiredArgsConstructor
@Slf4j
public class VectorController  {
    
    public DataLoader dataLoaderService;

    @PostMapping("/single/json")
    public <T> ResponseEntity<List<String>> createEmbeddings(@RequestBody T obj) throws Exception{
        List<String> result = dataLoaderService.vectorizeObject(obj, "solution");
        return ResponseEntity.ok(result);
    }
}
