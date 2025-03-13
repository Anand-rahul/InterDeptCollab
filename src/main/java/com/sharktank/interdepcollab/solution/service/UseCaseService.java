package com.sharktank.interdepcollab.solution.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sharktank.interdepcollab.solution.model.*;
import com.sharktank.interdepcollab.solution.repository.UseCaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UseCaseService {

    private final UseCaseRepository useCaseRepository;

    public Page<UseCase> getAllUseCases(Pageable pageable) {
        return useCaseRepository.findAll(pageable);
    }
    
    public UseCase addCommentAndUrl(Integer id, UseCase updatedUseCase) {
        UseCase currentUseCase = useCaseRepository.findById(id).orElseThrow();
        if (updatedUseCase.getComments() != null && !updatedUseCase.getComments().isEmpty()) {
            currentUseCase.setComments(updatedUseCase.getComments());
        }
        if (updatedUseCase.getDashboardURL() != null && !updatedUseCase.getDashboardURL().isEmpty()) {
            currentUseCase.setDashboardURL(updatedUseCase.getDashboardURL());
        }
        useCaseRepository.save(currentUseCase);
        return currentUseCase;
    }


}
