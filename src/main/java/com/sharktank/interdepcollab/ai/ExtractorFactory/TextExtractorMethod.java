package com.sharktank.interdepcollab.ai.ExtractorFactory;

public class TextExtractorMethod extends AbstractExtractorMethod{

    @Override
    public ITextExtractor factoryMethod() {
        return new TxtTextExtractor();
    }
}
