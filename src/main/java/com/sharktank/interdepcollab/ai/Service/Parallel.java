package com.sharktank.interdepcollab.ai.Service;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sharktank.interdepcollab.ai.Constants.SourceType;
import com.sharktank.interdepcollab.ai.DTO.SourceBase;
import com.sharktank.interdepcollab.ai.DTO.SourceDocumentBase;
import com.sharktank.interdepcollab.ai.Model.Optimax;
import com.sharktank.interdepcollab.configuration.MultiThreading;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class Parallel {
    
    @Autowired
    private MultiThreading multiThreading;

    @Autowired
    private DataLoader dataLoaderService;


    @Autowired
    private OptimaxService optimaxService;


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

    public void parallelSimilarityScore(List<String> comparisonList, String newSolutionString){
        multiThreading.executeAsync(()->{
            try{
                for (String str : comparisonList) {
                    Optimax op = optimaxService.fetchSimilarityScore(newSolutionString,str);
                    log.info("Similarity Score Calculated between {1} & {2}"+op.getSolId1(),op.getSolId2());
                }
            }catch(Exception ex){
                log.error("Error occured in checking Similarity of File:"+ex.getMessage());
            }
        });
    }
}
