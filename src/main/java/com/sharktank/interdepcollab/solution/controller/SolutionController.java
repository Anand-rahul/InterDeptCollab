package com.sharktank.interdepcollab.solution.controller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.solution.model.*;
import com.sharktank.interdepcollab.solution.service.SolutionService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;


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

    @PostMapping("/{id}/file")
    public ResponseEntity<Void> createFile(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws IOException {
        solutionService.addFile(file, id);
        return ResponseEntity.ok().build();
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

    @GetMapping("/{id}/file/{fileId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getMethodName(@PathVariable Integer id, @PathVariable Integer fileId) throws Exception{
        StreamingOutput blobStream = solutionService.getFileStream(fileId);
        return Response.ok(blobStream).build();
    }
    

}

