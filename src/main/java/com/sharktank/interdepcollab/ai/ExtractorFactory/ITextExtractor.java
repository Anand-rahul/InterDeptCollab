package com.sharktank.interdepcollab.ai.ExtractorFactory;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.core.io.InputStreamResource;

public interface ITextExtractor {
    List<Document> extractText(InputStreamResource resource) throws IOException;
}