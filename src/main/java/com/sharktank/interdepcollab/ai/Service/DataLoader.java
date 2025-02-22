package com.sharktank.interdepcollab.ai.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharktank.interdepcollab.ai.ExtractorFactory.AbstractExtractorMethod;
import com.sharktank.interdepcollab.ai.ExtractorFactory.JsonExtractorMethod;
import com.sharktank.interdepcollab.ai.ExtractorFactory.PdfExtractorMethod;
import com.sharktank.interdepcollab.ai.ExtractorFactory.TextExtractorMethod;
import com.sharktank.interdepcollab.ai.Model.VectorStore;
import com.sharktank.interdepcollab.ai.ParserStrategy.JsonParseStrategy;
import com.sharktank.interdepcollab.ai.ParserStrategy.ParsingStrategyFactory;
import com.sharktank.interdepcollab.ai.Repository.VectorRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
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
     
    @Value("${azure.blob.connection-string}")
    private String connectionString;


    @Transactional
    public  List<String> vectorizeObjectStrategy(String obj, String sourceType, String format) throws Exception {
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
        
        List<String> embeddingUUIDs = storeEmbeddings(textContents, "json", "", sourceType);
        
        log.info("Object ingestion completed for {} documents.", documents.size());
        return embeddingUUIDs;
    }

    @Transactional
    public <T> List<String> vectorizeObject(String obj, String sourceType) throws Exception {
        if (!List.of("solution").contains(sourceType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sourceType. Must be 'solution'");
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
        List<String> embeddingUUID=storeEmbeddings(List.of(new Document(jsonContent).getText()), "json", "", sourceType);
        
        log.info("Object ingestion completed.");
        return embeddingUUID;
    }

    @Transactional
    public List<String> vectorizeFileFactory(String path,String sourceType) throws Exception,IOException,URISyntaxException{
        if (!List.of("document", "solution", "requirement").contains(sourceType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sourceType. Must be 'document', 'solution', or 'requirement'");
        }

        InputStream inputStream = loadBlobStreamFromAzure(path);
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
        List<String> embeddingsUUID=storeEmbeddings(chunks, extension, filename, sourceType);

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
    //     List<String> chunks = splitText(content, 1000);

    //     log.info("Generating embeddings...");
    //     storeEmbeddings(chunks, extension, filename, sourceType);

    //     log.info("Document ingestion completed.");
    //     return "Vectorization of Blob File Completed!";
    // }

    private List<Document> extractTextFromPdf(InputStreamResource resource) throws IOException {
        var pdfConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                                .withNumberOfBottomTextLinesToDelete(1)
                                .withNumberOfTopPagesToSkipBeforeDelete(1)
                                .build()
                )
                .withPagesPerDocument(1)
                .build();
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource, pdfConfig);
        return pdfDocumentReader.get();
    }

    private List<Document> extractTextFromTxt(InputStreamResource resource) throws IOException {
        TextReader textDocumentReader = new TextReader(resource);
        return textDocumentReader.get();
    }

    private List<Document> extractTextFromJson(InputStreamResource resource) throws IOException {
        JsonReader jsonDocumentReader = new JsonReader(resource);
        return jsonDocumentReader.get();
    }

    public List<String> splitText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(length, i + chunkSize);
            chunks.add(text.substring(i, end));
        }
        return chunks;
    }

    private InputStream loadBlobStreamFromAzure(String path) throws URISyntaxException, IOException {
        URI blobUri = new URI(path);

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(blobUri.getHost())
                .blobName(blobUri.getPath().substring(1))
                .buildClient();

        byte[] blobBytes = blobClient.downloadContent().toBytes();
        return new ByteArrayInputStream(blobBytes);
    }


    private List<String> storeEmbeddings(List<String> chunks, String fileType, String fileName, String sourceType)throws Exception {
        List<VectorStore> vectorDataList = new ArrayList<>();
        List<String> embeddingsUUID=new ArrayList<>();
        for (String chunk : chunks) {
            float[] embedding = openAIEmbeddingService.getEmbeddingHttp(chunk);
            UUID sourceId = UUID.randomUUID();
            embeddingsUUID.add(sourceId.toString());
            Map<String, String> metadata = new HashMap<>();
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
        for (VectorStore vs : vectorDataList) {
            log.info(vs.toString());
        }

        vectorRepository.saveAll(vectorDataList);  
        return embeddingsUUID;
    }
}
