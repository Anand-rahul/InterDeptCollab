package com.sharktank.interdepcollab.file.repository; 
import com.sharktank.interdepcollab.file.model.File;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Integer>{
}