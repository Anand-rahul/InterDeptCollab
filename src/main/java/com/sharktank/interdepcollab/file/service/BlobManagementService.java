package com.sharktank.interdepcollab.file.service;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobCopyInfo;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.blob.specialized.BlobInputStream;
import com.sharktank.interdepcollab.exception.FileParentExistsException;
import com.sharktank.interdepcollab.file.model.FileMetadata;
import com.sharktank.interdepcollab.file.repository.FileMetadataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlobManagementService {

    private final FileMetadataRepository fileMetadataRepository;
    private final BlobServiceClient blobServiceClient;
   
    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;

    @Transactional
    public FileMetadata uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String blobFileName = "UNTAGGED_" + fileName;
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobFileName);

        blobClient.upload(file.getInputStream(), file.getSize(), false);
        String fileUrl = blobClient.getBlobUrl();

        FileMetadata fileMetadata = FileMetadata.builder()
                .docUrl(fileUrl)
                .name(blobFileName)
                .originalName(fileName)
                .size(file.getSize())
                .build();

        return fileMetadataRepository.save(fileMetadata);
    }

    @Transactional
    public FileMetadata uploadFile(MultipartFile file, String parentType, Integer parentId) throws IOException {
        String fileName = file.getOriginalFilename();
        String blobFileName = String.format("%s_%s_%s", parentType, parentId, fileName);
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobFileName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);
        String fileUrl = blobClient.getBlobUrl();

        FileMetadata fileMetadata = FileMetadata.builder()
                .docUrl(fileUrl)
                .parentType(parentType)
                .parentId(parentId)
                .name(blobFileName)
                .originalName(fileName)
                .size(file.getSize())
                .build();

        return fileMetadataRepository.save(fileMetadata);
    }

    // BUG: On rollback file rename is not rolled back
    @Transactional
    public FileMetadata tagFileToParent(Integer fileId, String parentType, Integer parentId) throws FileParentExistsException{
        FileMetadata fileMetadata = fileMetadataRepository.getReferenceById(fileId);
        
        log.info("Fetched Data: {}",fileMetadata.toString());

        if(fileMetadata.getParentId() != null){
            throw new FileParentExistsException("File already tagged to parent");
        }

        String newFileName = String.format("%s_%s_%s", parentType, parentId, fileMetadata.getOriginalName());
        String prevFileName = fileMetadata.getName();      

        //TODO: Add auto file deletion

        String fileUrl = renameBlobWithMetadata(prevFileName, newFileName);
        
        fileMetadata.setParentId(parentId);
        fileMetadata.setParentType(parentType);
        fileMetadata.setDocUrl(fileUrl);
        fileMetadata.setName(newFileName);
        
        log.info("Updating Data: {}",fileMetadata.toString());
        return fileMetadataRepository.save(fileMetadata);
    }

    public String renameBlobWithMetadata(String oldBlobName, String newBlobName) {

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient sourceBlob = containerClient.getBlobClient(oldBlobName);
        BlobClient destinationBlob = containerClient.getBlobClient(newBlobName);

        // Check if source exists
        if (!sourceBlob.exists()) {
            throw new IllegalArgumentException("Source blob " + oldBlobName + " does not exist");
        }

        // Check if destination already exists
        if (destinationBlob.exists()) {
            throw new IllegalArgumentException("Destination blob " + newBlobName + " already exists");
        }

        // Create SAS token for source blob with read permissions
        String sasToken = getSasToken(sourceBlob);

        // Start copy operation using SAS token
        String sourceBlobUrl = sourceBlob.getBlobUrl() + "?" + sasToken;
        PollResponse<BlobCopyInfo> pollResponse = destinationBlob.beginCopy(sourceBlobUrl, Duration.ofSeconds(1))
                .waitForCompletion(Duration.ofSeconds(10));

        if (pollResponse.getStatus() == LongRunningOperationStatus.SUCCESSFULLY_COMPLETED) {
            sourceBlob.delete();
            return destinationBlob.getBlobUrl();
        } else {
            try {
                destinationBlob.delete();
            } catch (Exception e) {
                // Log the cleanup failure but throw the original error
                log.error("Failed to clean up failed copy: " + e.getMessage());
            }
            throw new RuntimeException("Blob copy failed or was aborted");
        }
    }
    
    private String getSasToken(BlobClient client){
          // Get source blob properties and metadata
        BlobProperties sourceProperties = client.getProperties();
        Map<String, String> metadata = sourceProperties.getMetadata();

        // Create SAS token for source blob with read permissions
        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(2);
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission);
        return client.generateSas(values);
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
