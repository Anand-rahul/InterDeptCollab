package com.sharktank.interdepcollab.ai.DTO;

import com.sharktank.interdepcollab.solution.model.SolutionBaseDTO;

public class SimilarityResponseDTO {
    public double similarityScore;
    public SolutionBaseDTO solutionBaseDTO;

    public SimilarityResponseDTO(double similarityScore,SolutionBaseDTO solutionBaseDTO){
        this.similarityScore=similarityScore;
        this.solutionBaseDTO=solutionBaseDTO;
    }
    
}