package com.sharktank.interdepcollab.file.controller;

import java.io.IOException;
import java.io.InputStream;

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

import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.service.BlobManagementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/file")
public class FileController {

    private final BlobManagementService fileService;

     @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Integer id) throws Exception {
        InputStream blobStream = fileService.getFile(id);
        InputStreamResource resource = new InputStreamResource(blobStream);
        
        // Get file metadata for content length and filename
        FileMetadata fileMetadata = fileService.getMetadata(id);
        
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