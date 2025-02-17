package com.sharktank.interdepcollab.file.repository; 
import com.sharktank.interdepcollab.file.model.FileMetadata;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Integer>{
}