package com.sharktank.interdepcollab.file.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.sharktank.interdepcollab.ai.Constants.SourceType;
import com.sharktank.interdepcollab.ai.Model.SourceBase;
import com.sharktank.interdepcollab.ai.Model.SourceDocumentBase;
import com.sharktank.interdepcollab.ai.Service.DataLoader;
import com.sharktank.interdepcollab.ai.Service.Parallel;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.service.BlobManagementService;
import com.sharktank.interdepcollab.solution.model.Solution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/api/file")
public class FileController {

    private final BlobManagementService fileService;
    private final Parallel parallelService;

     @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Integer id) throws Exception {
        InputStream blobStream = fileService.getFile(id);
        
        
        InputStreamResource resource = new InputStreamResource(blobStream);
        
        // Get file metadata for content length and filename
        FileMetadata fileMetadata = fileService.getMetadata(id);
        
        // Send to AI Background vectorizing service
        try {
            SourceDocumentBase solutionVectorize = new SourceDocumentBase(SourceType.SOLUTION_DOCUMENT.toString(),
                    fileMetadata.getParentId(), fileService.getFile(id),fileMetadata.getOriginalName());
            log.info("Vectorising File -> {}", fileMetadata.getId());
            parallelService.parallelVectorizeFile(solutionVectorize, SourceType.SOLUTION_DOCUMENT,fileMetadata.getParentId().toString());
        } catch (Exception ex) {
            log.error("Exception while vectorising file", ex);
        }

        return ResponseEntity.ok()
                .contentLength(fileMetadata.getSize()) // If you have the file size
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileMetadata.getOriginalName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping()
    public ResponseEntity<FileMetadata> createFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileMetadata fileMetadata = fileService.uploadFile(file);
        return ResponseEntity.ok(fileMetadata);
    }
}