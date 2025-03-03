package com.sharktank.interdepcollab.ai.ExtractorFactory;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.InputStreamResource;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PdfTextExtractor implements ITextExtractor{

    @Override
    public List<Document> extractText(InputStreamResource resource) throws IOException {
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
        log.info("DocsFetched:"+pdfDocumentReader.get().toString());
        return pdfDocumentReader.get();
    }
    
}
