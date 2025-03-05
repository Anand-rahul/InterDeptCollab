package com.sharktank.interdepcollab.ai.ExtractorFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PdfTextExtractor implements ITextExtractor{

    @Override
    public List<Document> extractText(InputStreamResource resource) throws IOException {
        var pdfConfig = PdfDocumentReaderConfig.builder()
                // .withPageExtractedTextFormatter(
                //         new ExtractedTextFormatter.Builder()
                //                 .withNumberOfBottomTextLinesToDelete(1)
                //                 .withNumberOfTopPagesToSkipBeforeDelete(1)
                //                 .build()
                // )
                .withPagesPerDocument(1)
                .build();        
        Map<String,String> output=pdfParser(resource,pdfConfig);
        Document documentFetched = new Document(output.get("extractedText"));
        return List.of(documentFetched);
    }

    private Map<String,String> pdfParser(Resource pdfResource,PdfDocumentReaderConfig config){
        Map<String,String> opMap=new HashMap<>();
        try (InputStream inputStream = pdfResource.getInputStream()) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            PDFParser parser = new PDFParser();
            parser.parse(inputStream, handler, metadata, new ParseContext());
            opMap.put("extractedText" , handler.toString());
            opMap.put("resourceFileName" , pdfResource.getFilename());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error extracting text from PDF", e);
        }
        return opMap;
    }
    private ByteArrayResource convertToByteArrayResource(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        inputStream.transferTo(buffer); // Read input stream to memory
        return new ByteArrayResource(buffer.toByteArray());
    }
}
