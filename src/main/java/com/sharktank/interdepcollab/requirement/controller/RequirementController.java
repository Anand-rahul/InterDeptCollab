package com.sharktank.interdepcollab.requirement.controller;

import com.sharktank.interdepcollab.requirement.service.RequirementService;

import lombok.RequiredArgsConstructor;

import com.github.fge.jsonpatch.JsonPatchException;
import com.sharktank.interdepcollab.devops.model.UserStory;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requirement")
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping
    public ResponseEntity<RequirementDetailedDTO> createRequirement(@RequestBody RequirementInput requirement) {
        RequirementDetailedDTO createdRequirement = requirementService.createRequirement(requirement);
        return ResponseEntity.ok(createdRequirement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequirementDetailedDTO> updateRequirement(@PathVariable Integer id,
            @RequestBody RequirementInput requirement) throws JsonPatchException {
        RequirementDetailedDTO updatedRequirement = requirementService.updateRequirement(id, requirement);
        return ResponseEntity.ok(updatedRequirement);
    }

    @GetMapping
    public ResponseEntity<Page<RequirementBaseDTO>> getAllRequirement(
            @SortDefault(sort = "createdDate") @PageableDefault(size = 20) Pageable pageable) {
        Page<RequirementBaseDTO> requirements = requirementService.getAllRequirements(pageable);
        return ResponseEntity.ok(requirements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequirementDetailedDTO> getRequirement(@PathVariable Integer id) {
        RequirementDetailedDTO requirement = requirementService.getRequirement(id);
        return ResponseEntity.ok(requirement);
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<FileMetadata> createFile(@PathVariable Integer id, @RequestParam("file") MultipartFile file)
            throws IOException {
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

    @PutMapping("/{id}/status/returnToPool")
    public ResponseEntity<RequirementBaseDTO> revertToNew(@PathVariable Integer id) {
        RequirementBaseDTO requirement = requirementService.updateStatus(id, Status.NEW, null);
        return ResponseEntity.ok(requirement);
    }

    @PutMapping("/{id}/status/discussion")
    public ResponseEntity<RequirementBaseDTO> moveToDicussion(@PathVariable Integer id) {
        RequirementBaseDTO requirement = requirementService.updateStatus(id, Status.DISCUSSION, null);
        return ResponseEntity.ok(requirement);
    }

    @PutMapping("/{id}/status/accepted")
    public ResponseEntity<RequirementBaseDTO> moveToAccepted(@PathVariable Integer id, @RequestBody UserStory userStory) {
        RequirementBaseDTO requirement = requirementService.updateStatus(id, Status.IN_PROGRESS, userStory);
        return ResponseEntity.ok(requirement);
    }

    @PutMapping("/{id}/status/completed")
    public ResponseEntity<RequirementBaseDTO> moveToCompleted(@PathVariable Integer id) {
        RequirementBaseDTO requirement = requirementService.updateStatus(id, Status.COMPLETED, null);
        return ResponseEntity.ok(requirement);
    }
}