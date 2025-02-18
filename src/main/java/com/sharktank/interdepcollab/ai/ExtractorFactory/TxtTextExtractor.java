package com.sharktank.interdepcollab.ai.ExtractorFactory;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.InputStreamResource;

public class TxtTextExtractor implements ITextExtractor{

    @Override
    public List<Document> extractText(InputStreamResource resource) throws IOException {
        TextReader textDocumentReader = new TextReader(resource);
        return textDocumentReader.get();
    }

}
