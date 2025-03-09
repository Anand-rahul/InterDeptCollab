package com.sharktank.interdepcollab.ai.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.ai.DTO.OptimaxRequestDTO;
import com.sharktank.interdepcollab.ai.DTO.SimilarityResponseDTO;
import com.sharktank.interdepcollab.solution.model.SolutionBaseDTO;

import lombok.extern.slf4j.Slf4j;

import com.sharktank.interdepcollab.ai.Model.Optimax;
import com.sharktank.interdepcollab.ai.Service.OptimaxService;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/fetch")
    public ResponseEntity<?> getSimilarSolutions(@RequestParam("threshold") double threshold) {
        // Validate threshold is between 0 and 100
        if (threshold < 0 || threshold > 100) {
            return ResponseEntity
                .badRequest()
                .body("Threshold must be between 0 and 100");
        }
        return ResponseEntity.ok(optimaxService.getSimilarSolutions((double)(threshold/100)));
    }
    
}
