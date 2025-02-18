package com.sharktank.interdepcollab.ai.ExtractorFactory;

public class PdfExtractorMethod extends AbstractExtractorMethod {

    @Override
    public ITextExtractor factoryMethod() {
        return new PdfTextExtractor();
    }
    
}
