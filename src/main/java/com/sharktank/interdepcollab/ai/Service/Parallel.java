package com.sharktank.interdepcollab.ai.Service;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sharktank.interdepcollab.ai.Constants.SourceType;
import com.sharktank.interdepcollab.ai.Model.SourceBase;
import com.sharktank.interdepcollab.ai.Model.SourceDocumentBase;
import com.sharktank.interdepcollab.configuration.MultiThreading;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class Parallel {
    
    @Autowired
    private MultiThreading multiThreading;

    @Autowired
    private DataLoader dataLoaderService;


    public <T extends SourceBase> void parallelVectorizeObject(JsonNode obj, SourceType sourceType, String id) throws Exception {
        multiThreading.executeAsync(() -> {
            try {
                dataLoaderService.vectorizeObject(obj, sourceType, id);
            } catch (Exception e) {
                log.error("Error occured in vectorization of Object:"+e.getMessage());
            }
        });
    }

    public <T extends SourceDocumentBase> void parallelVectorizeFile(T obj, SourceType sourceType, String id) {
        multiThreading.executeAsync(() -> {
            try {
                dataLoaderService.vectorizeFile(obj, sourceType, id);
            } catch (Exception e) {
                log.error("Error occured in vectorization of File:"+e.getMessage());
            }
        });
    }
}
