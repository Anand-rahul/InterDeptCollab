package com.sharktank.interdepcollab.solution.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.solution.model.*;
import com.sharktank.interdepcollab.solution.service.SolutionService;

@RestController
@RequestMapping("/api/solution")
public class SolutionController {

    @Autowired
    private SolutionService solutionService;

    @PostMapping
    public ResponseEntity<SolutionDTO> createSolution(@RequestBody SolutionDTO solution) {
        SolutionDTO createdSolution = solutionService.createSolution(solution);
        return ResponseEntity.ok(createdSolution);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolutionDTO> updateSolution(@PathVariable Integer id, @RequestBody SolutionDTO solution) throws JsonPatchException {
        SolutionDTO updatedSolution = solutionService.updateSolution(id, solution);
        return ResponseEntity.ok(updatedSolution);
    }

    @GetMapping
    public ResponseEntity<Page<SolutionDTO>> getAllSolutions(@SortDefault(sort = "viewCount") @PageableDefault(size = 20) Pageable pageable) {
        Page<SolutionDTO> solutions = solutionService.getAllSolutions(pageable);
        return ResponseEntity.ok(solutions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolutionDTO> getSolution(@PathVariable Integer id) {
        SolutionDTO solution = solutionService.getSolution(id);
        return ResponseEntity.ok(solution);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Boolean> likeSolution(@PathVariable Integer id) {
        Boolean likedSolution = solutionService.toggleLike(id);
        return ResponseEntity.ok(likedSolution);
    }
}
