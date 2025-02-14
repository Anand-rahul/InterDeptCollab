package com.sharktank.interdepcollab.ai.ExtractorFactory;

public class JsonExtractorMethod extends AbstractExtractorMethod{

    @Override
    public ITextExtractor factoryMethod() {
        return new JsonTextExtractor();
    }
    
}
