package com.sharktank.interdepcollab.ai.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class DataLoader {

    @Autowired
    private VectorStore vectorStore;

    // Azure Blob Storage connection details
    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String connectionString;

    public String Vectorize(String path) throws IOException, URISyntaxException {
        
        InputStream inputStream = loadBlobStreamFromAzure(path);

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().withPagesPerDocument(1).build();
        PagePdfDocumentReader reader = new PagePdfDocumentReader(inputStreamResource, config); // Use InputStream

        var textSplitter = new TokenTextSplitter();

        vectorStore.accept(textSplitter.apply(reader.get()));

        return "Vectorization Completed !";
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
}
