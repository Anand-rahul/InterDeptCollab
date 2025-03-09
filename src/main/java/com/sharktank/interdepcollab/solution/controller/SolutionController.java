package com.sharktank.interdepcollab.solution.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.solution.model.*;
import com.sharktank.interdepcollab.solution.service.SolutionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/solution")
public class SolutionController {

    private final SolutionService solutionService;

    @PostMapping
    public ResponseEntity<SolutionDetailedDTO> createSolution(@RequestBody SolutionInput solution) throws Exception {
        SolutionDetailedDTO createdSolution = solutionService.createSolution(solution);
        return ResponseEntity.ok(createdSolution);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolutionDetailedDTO> updateSolution(@PathVariable Integer id, @RequestBody SolutionInput solution) throws JsonPatchException {
        SolutionDetailedDTO updatedSolution = solutionService.updateSolution(id, solution);
        return ResponseEntity.ok(updatedSolution);
    }

    @GetMapping
    public ResponseEntity<Page<SolutionBaseDTO>> getAllSolutions(@SortDefault(sort = "viewCount") @PageableDefault(size = 20) Pageable pageable) {
        Page<SolutionBaseDTO> solutions = solutionService.getAllSolutions(pageable);
        return ResponseEntity.ok(solutions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolutionDetailedDTO> getSolution(@PathVariable Integer id) {
        SolutionDetailedDTO solution = solutionService.getSolution(id);
        return ResponseEntity.ok(solution);
    }

    @GetMapping("/{id}/faqs")
    public ResponseEntity<Set<FAQ>> getFAQs(@PathVariable Integer id) {
        Set<FAQ> faqs = solutionService.getFAQs(id);
        return ResponseEntity.ok(faqs);
    }
    
    @PostMapping("/{id}/faqs")
    public ResponseEntity<Set<FAQ>> addFAQs(@PathVariable Integer id, @RequestBody Set<FAQ> faqs) {
        Set<FAQ> finalFaqs = solutionService.addFAQs(id, faqs);
        return ResponseEntity.ok(finalFaqs);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Boolean> likeSolution(@PathVariable Integer id) {
        Boolean likedSolution = solutionService.toggleLike(id);
        return ResponseEntity.ok(likedSolution);
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<FileMetadata> createFile(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws IOException {
        FileMetadata fileMetadata = solutionService.addFile(file, id);
        return ResponseEntity.ok(fileMetadata);
    }

    @DeleteMapping("/{id}/file/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id, @PathVariable Integer fileId) throws IOException {
        solutionService.removeFile(fileId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Map<String, Integer>> getAllFiles(@PathVariable Integer id) {
        Map<String, Integer> files = solutionService.getAllFiles(id);
        return ResponseEntity.ok(files);
    }
}

