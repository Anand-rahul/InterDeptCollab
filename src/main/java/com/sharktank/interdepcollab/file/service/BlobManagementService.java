package com.sharktank.interdepcollab.file.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.specialized.BlobInputStream;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.repository.FileMetadataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlobManagementService {

    private final FileMetadataRepository fileMetadataRepository;
    private final BlobServiceClient blobServiceClient;
   
    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    @Transactional
    public FileMetadata uploadFile(MultipartFile file, String parentType, Integer parentId) throws IOException{
       String fileName = file.getOriginalFilename();
       BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(fileName);

         blobClient.upload(file.getInputStream(), file.getSize(), true);
         String fileUrl = blobClient.getBlobUrl();

            FileMetadata fileMetadata = FileMetadata.builder()
                    .docUrl(fileUrl)
                    .parentType(parentType)
                    .parentId(parentId)
                    .name(fileName)
                    .size(file.getSize())
                    .build();

                    return fileMetadataRepository.save(fileMetadata);
    }

    @Transactional
    public FileMetadata deleteFile(Integer fileId){
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId).orElseThrow();
        deleteFile(fileMetadata);
        return fileMetadata;
    }

    @Transactional
    public void deleteFile(FileMetadata fileMetadata){
        String fileName = fileMetadata.getName();
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(fileName);
        blobClient.delete();
        fileMetadataRepository.delete(fileMetadata);
    }

    public FileMetadata getMetadata(Integer fileId){
        return fileMetadataRepository.findById(fileId).orElseThrow();
    }
    
    public BlobInputStream getFile(Integer fileId){
        FileMetadata fileMetadata = getMetadata(fileId);
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(fileMetadata.getName());
        return blobClient.openInputStream();
    }
}
