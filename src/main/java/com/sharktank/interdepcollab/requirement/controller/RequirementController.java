package com.sharktank.interdepcollab.requirement.controller;

import com.sharktank.interdepcollab.requirement.service.RequirementService;

import lombok.RequiredArgsConstructor;

import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.requirement.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requirement")
public class RequirementController {

    private final RequirementService requirementService;

   @PostMapping
    public ResponseEntity<RequirementDTO> createRequirement(@RequestBody RequirementInput requirement) {
        RequirementDTO createdRequirement = requirementService.createRequirement(requirement);
        return ResponseEntity.ok(createdRequirement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequirementDTO> updateRequirement(@PathVariable Integer id, @RequestBody RequirementInput requirement) throws JsonPatchException {
        RequirementDTO updatedRequirement = requirementService.updateRequirement(id, requirement);
        return ResponseEntity.ok(updatedRequirement);
    }

    @GetMapping
    public ResponseEntity<Page<RequirementDTO>> getAllRequirement(@SortDefault(sort = "createdDate") @PageableDefault(size = 20) Pageable pageable) {
        Page<RequirementDTO> requirements = requirementService.getAllRequirements(pageable);
        return ResponseEntity.ok(requirements);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RequirementDTO> getRequirement(@PathVariable Integer id) {
        RequirementDTO requirement = requirementService.getRequirement(id);
        return ResponseEntity.ok(requirement);
    }

     @PostMapping("/{id}/file")
    public ResponseEntity<FileMetadata> createFile(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws IOException {
        FileMetadata fileMetadata = requirementService.addFile(file, id);
        return ResponseEntity.ok(fileMetadata);
    }

    @DeleteMapping("/{id}/file/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id, @PathVariable Integer fileId) throws IOException {
        requirementService.removeFile(fileId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Map<String, Integer>> getAllFiles(@PathVariable Integer id) {
        Map<String, Integer> files = requirementService.getAllFiles(id);
        return ResponseEntity.ok(files);
    }
}