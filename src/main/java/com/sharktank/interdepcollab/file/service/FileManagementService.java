package com.sharktank.interdepcollab.file.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileManagementService {
    void uploadFile(String filePath, String fileName, Object Parent, MultipartFile file);
    void deleteFile(String fileId);
    MultipartFile downloadFile(String fileId);
    List<String> listFiles(String parentType);
}
