package com.sharktank.interdepcollab.ai.ExtractorFactory;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.InputStreamResource;

public class JsonTextExtractor implements ITextExtractor{

    @Override
    public List<Document> extractText(InputStreamResource resource) throws IOException {
        JsonReader jsonDocumentReader = new JsonReader(resource);
        return jsonDocumentReader.get();
    }

}
