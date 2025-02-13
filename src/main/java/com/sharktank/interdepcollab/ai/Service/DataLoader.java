package com.sharktank.interdepcollab.ai.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharktank.interdepcollab.ai.Model.VectorData;
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
    private ObjectMapper objectMapper;  
    @Value("${azure.blob.connection-string}")
    private String connectionString;

    @Transactional
    public String vectorizeBlobFile(String path, String sourceType) throws IOException, URISyntaxException {
        if (!List.of("document", "solution", "requirement").contains(sourceType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sourceType. Must be 'document', 'solution', or 'requirement'");
        }

        InputStream inputStream = loadBlobStreamFromAzure(path);
        InputStreamResource resource = new InputStreamResource(inputStream);

        String filename = resource.getFilename();
        String extension = FilenameUtils.getExtension(filename);

        log.info("Processing file: {} as {}", filename, sourceType);
        
        List<Document> documentList;
        switch (extension) {
            case "pdf":
                documentList = extractTextFromPdf(resource);
                break;
            case "txt":
                documentList = extractTextFromTxt(resource);
                break;
            case "json":
                documentList = extractTextFromJson(resource);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type: " + extension);
        }

        String content = documentList.stream().map(Document::getText).collect(Collectors.joining("\n"));
        log.info("Splitting document into chunks...");
        List<String> chunks = splitText(content, 1000);

        log.info("Generating embeddings...");
        storeEmbeddings(chunks, extension, filename, sourceType);

        log.info("Document ingestion completed.");
        return "Vectorization of Blob File Completed!";
    }

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


    private void storeEmbeddings(List<String> chunks, String fileType, String fileName, String sourceType) {
        List<VectorData> vectorDataList = new ArrayList<>();

        for (String chunk : chunks) {
            float[] embedding = openAIEmbeddingService.getEmbedding(chunk);
            UUID sourceId = UUID.randomUUID();

            Map<String, String> metadata = new HashMap<>();
            metadata.put("sourceType",sourceType);
            metadata.put("fileName", fileName);
            metadata.put("fileType", fileType);
            metadata.put("chunkSize", String.valueOf(chunk.length()));

            JsonNode jsonData = objectMapper.valueToTree(metadata);

            vectorDataList.add(new VectorData(sourceType, sourceId, chunk, jsonData, embedding));
        }

        vectorRepository.saveAll(vectorDataList);  
    }
}
