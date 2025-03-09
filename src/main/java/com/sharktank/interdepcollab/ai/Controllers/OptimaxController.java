package com.sharktank.interdepcollab.ai.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.ai.DTO.OptimaxRequestDTO;
import com.sharktank.interdepcollab.ai.Model.Optimax;
import com.sharktank.interdepcollab.ai.Service.OptimaxService;

import groovy.util.logging.Slf4j;

@RestController
@RequestMapping("/v1/optimize")
@Slf4j
public class OptimaxController {
    
    @Autowired
    private OptimaxService optimaxService;

    @PostMapping("/score")
    public ResponseEntity<Optimax> fetchOptimaxScore(@RequestBody OptimaxRequestDTO json) throws Exception {
        
        Optimax result = optimaxService.fetchSimilarityScore(json.stringInitial,json.stringMatch);
        return ResponseEntity.ok(result);
    }
}
