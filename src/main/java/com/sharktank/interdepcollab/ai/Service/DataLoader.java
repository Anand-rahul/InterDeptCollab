package com.sharktank.interdepcollab.ai.Service;

import com.azure.storage.blob.BlobServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharktank.interdepcollab.ai.Constants.SourceType;
import com.sharktank.interdepcollab.ai.ExtractorFactory.AbstractExtractorMethod;
import com.sharktank.interdepcollab.ai.ExtractorFactory.JsonExtractorMethod;
import com.sharktank.interdepcollab.ai.ExtractorFactory.PdfExtractorMethod;
import com.sharktank.interdepcollab.ai.ExtractorFactory.TextExtractorMethod;
import com.sharktank.interdepcollab.ai.Model.SourceBase;
import com.sharktank.interdepcollab.ai.Model.SourceDocumentBase;
import com.sharktank.interdepcollab.ai.Model.VectorStore;
import com.sharktank.interdepcollab.ai.ParserStrategy.JsonParseStrategy;
import com.sharktank.interdepcollab.ai.ParserStrategy.ParsingStrategyFactory;
import com.sharktank.interdepcollab.ai.Repository.VectorRepository;
import com.sharktank.interdepcollab.configuration.MultiThreading;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataLoader {

    @Autowired
    private VectorRepository vectorRepository;  

    @Autowired
    private OpenAIEmbeddingService openAIEmbeddingService;  

    @Autowired
    private ParsingStrategyFactory parsingStrategyFactory;

    @Autowired
    private ObjectMapper objectMapper; 

    //Startegy method for Vectorization 
    @Autowired
    private MultiThreading multiThreading;


    @Transactional
    public  List<String> vectorizeObjectStrategy(String obj, String sourceType, String format){
        if (!List.of("solution").contains(sourceType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sourceType. Must be 'solution'");
        }
        
        log.info("Processing obj: {}, format: {}", obj, format);
        
        JsonParseStrategy strategy = parsingStrategyFactory.getStrategy(format);
        
        List<Document> documents;
        try {
            documents = strategy.parse(obj);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON with format: {}", format, e);
            throw new RuntimeException("Error parsing JSON", e);
        }
        
        log.info("Generating embeddings for {} documents...", documents.size());
        List<String> textContents = documents.stream()
                .map(Document::getText)
                .collect(Collectors.toList());
        
        List<String> embeddingUUIDs = storeEmbeddings(textContents, "json", "", sourceType,"");
        
        log.info("Object ingestion completed for {} documents.", documents.size());
        return embeddingUUIDs;
    }

    //Method for object vectorization(Final to be used in multithreading)
    @Transactional
    public <T extends SourceBase> List<String> vectorizeObject(JsonNode obj, SourceType sourceType, String id)  {
        if (sourceType != SourceType.SOLUTION ) {
            throw new IllegalArgumentException("Invalid sourceType. Must be 'SOLUTION' and object source type must match.");
        }
        if (!obj.has("sourceType") || !obj.has("id")) {
            throw new IllegalArgumentException("Missing required fields: 'sourceType' and/or 'id'.");
        }
        log.info("Processing obj: {}", obj);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent;
        try {
            jsonContent = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON", e);
            throw new RuntimeException("Error converting object to JSON", e);
        }
            
        log.info("Generating embeddings...");
        List<String> embeddingUUID = storeEmbeddings(List.of(new Document(jsonContent).getText()), "json", "", "SOLUTION", id);
        
        log.info("Object ingestion completed.");
        return embeddingUUID;
    }


    //Method for file vectorization(Final to be used in multithreading)
    @Transactional
    public <T extends SourceDocumentBase> List<String> vectorizeFile(T obj, SourceType sourceType, String id) throws Exception {
        List<String> ids = new ArrayList<>();
        
        if (sourceType == SourceType.SOLUTION_DOCUMENT) {
            for (var inputs : obj.getDocuments()) {
                InputStreamResource resource = new InputStreamResource(inputs);

                String filename = resource.getFilename();
                String extension = FilenameUtils.getExtension(filename);

                log.info("Processing file: {} as {}", filename, sourceType);
                List<Document> textChunks;
                
                AbstractExtractorMethod extractorMethod;
                if ("pdf".equalsIgnoreCase(extension)) {
                    extractorMethod = new PdfExtractorMethod();
                } else if ("txt".equalsIgnoreCase(extension)) {
                    extractorMethod = new TextExtractorMethod();
                } else if ("json".equalsIgnoreCase(extension)) {
                    extractorMethod = new JsonExtractorMethod();
                } else {
                    throw new IllegalArgumentException("Unsupported file type: " + extension);
                }
                
                textChunks = extractorMethod.factoryMethod().extractText(resource);

                String content = textChunks.stream().map(Document::getText).collect(Collectors.joining("\n"));

                log.info("Splitting document into chunks...");
                List<String> chunks = splitText(content, 1000);

                log.info("Generating embeddings...");
                List<String> embeddingsUUID = storeEmbeddings(chunks, extension, filename, "SOLUTION_DOCUMENT", id);

                ids.addAll(embeddingsUUID);
            }
        }
        log.info("Document ingestion completed.");
        return ids;
    }

    // @Transactional
    // public <T extends SourceDocumentBase> List<String> vectorizeFile(T obj , SourceType sourceType, String id)throws Exception{
    //     List<String> ids=new ArrayList<>();
    //     if(sourceType==SourceType.SOLUTION_DOCUMENT && obj.getSourceType()==sourceType){
            
    //         for(var inputs:obj.getDocuments()){
    //             List<String> idsSubset=vectorizeFileFactory(inputs, sourceType,id);
    //             ids.addAll(idsSubset);
    //         }
    //     }
    //     return ids;
    // }


    @Transactional
    public List<String> vectorizeFileFactory(InputStream inputStream,SourceType sourceType,String id) throws Exception,IOException,URISyntaxException{

        InputStreamResource resource = new InputStreamResource(inputStream);

        String filename = resource.getFilename();
        String extension = FilenameUtils.getExtension(filename);

        log.info("Processing file: {} as {}", filename, sourceType);
        List<Document> textChunks;
        if(extension=="pdf"){
            AbstractExtractorMethod pdfExtractorMethod=new PdfExtractorMethod();
            textChunks=pdfExtractorMethod.factoryMethod().extractText(resource);
        }else if(extension=="txt"){
            AbstractExtractorMethod txtExtractorMethod=new TextExtractorMethod();
            textChunks=txtExtractorMethod.factoryMethod().extractText(resource);
        }else if(extension=="json"){
            AbstractExtractorMethod jsonExtractorMethod=new JsonExtractorMethod();
            textChunks=jsonExtractorMethod.factoryMethod().extractText(resource);
        }else{
            throw new IllegalArgumentException("Unsupported file type: " + extension);
        }

        String content = textChunks.stream().map(Document::getText).collect(Collectors.joining("\n"));
        
        log.info("Splitting document into chunks...");
        List<String> chunks = splitText(content, 1000);

        log.info("Generating embeddings...");
        List<String> embeddingsUUID=storeEmbeddings(chunks, extension, filename, "SOLUTION_DOCUMENT",id);

        log.info("Document ingestion completed.");
        return embeddingsUUID;
    }

    // @Transactional
    // public String vectorizeFile(String path, String sourceType) throws IOException, URISyntaxException {
    //     if (!List.of("document", "solution", "requirement").contains(sourceType.toLowerCase())) {
    //         throw new IllegalArgumentException("Invalid sourceType. Must be 'document', 'solution', or 'requirement'");
    //     }
    //     InputStream inputStream = loadBlobStreamFromAzure(path);
    //     InputStreamResource resource = new InputStreamResource(inputStream);
    //     String filename = resource.getFilename();
    //     String extension = FilenameUtils.getExtension(filename);
    //     log.info("Processing file: {} as {}", filename, sourceType);
    //     List<Document> documentList;
    //     switch (extension) {
    //         case "pdf":
    //             documentList = extractTextFromPdf(resource);
    //             break;
    //         case "txt":
    //             documentList = extractTextFromTxt(resource);
    //             break;
    //         case "json":
    //             documentList = extractTextFromJson(resource);
    //             break;
    //         default:
    //             throw new IllegalArgumentException("Unsupported file type: " + extension);
    //     }
    //     String content = documentList.stream().map(Document::getText).collect(Collectors.joining("\n"));
    //     log.info("Splitting document into chunks...");
    //     List<String> chunks = splitText(content, 1000)
    //     log.info("Generating embeddings...");
    //     storeEmbeddings(chunks, extension, filename, sourceType);
    //     log.info("Document ingestion completed.");
    //     return "Vectorization of Blob File Completed!";
    // }

    public List<String> splitText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(length, i + chunkSize);
            chunks.add(text.substring(i, end));
        }
        return chunks;
    }


    public List<String> storeEmbeddings(List<String> chunks, String fileType, String fileName, String sourceType,String id) {
        List<VectorStore> vectorDataList = new ArrayList<>();
        List<String> embeddingsUUID=new ArrayList<>();
        for (String chunk : chunks) {
            float[] embedding= new float[1536];
            try {
                embedding = openAIEmbeddingService.getEmbeddingHttp(chunk);
            } catch (Exception e) {
                log.error("Error in Fetching Embedding:"+e.getMessage());
            }
            UUID sourceId = UUID.randomUUID();
            embeddingsUUID.add(sourceId.toString());
            Map<String, String> metadata = new HashMap<>();
            metadata.put("sourceId", id);
            metadata.put("sourceType",sourceType);
            metadata.put("fileName", fileName);
            metadata.put("fileType", fileType);
            metadata.put("chunkSize", String.valueOf(chunk.length()));

            try {
                String jsonData = objectMapper.writeValueAsString(metadata);  
                vectorDataList.add(new VectorStore(sourceType, sourceId, chunk, jsonData, embedding));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting metadata to JSON string", e);
            }
        }

        vectorRepository.saveAll(vectorDataList);  
        return embeddingsUUID;
    }
    public String getVectorString(float[] embedding){

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String delimiter = ","; 
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) {
                sb.append(delimiter);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
