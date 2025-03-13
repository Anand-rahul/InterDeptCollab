package com.sharktank.interdepcollab.solution.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.solution.model.SolutionBaseDTO;
import com.sharktank.interdepcollab.solution.model.UseCase;
import com.sharktank.interdepcollab.solution.service.UseCaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/usecase")
public class UseCaseController {

    private final UseCaseService useCaseService;

    @GetMapping
    public ResponseEntity<Page<UseCase>> getAllUseCases(@SortDefault(sort = "createdDate") @PageableDefault(size = 20) Pageable pageable) {
        Page<UseCase> usecases = useCaseService.getAllUseCases(pageable);
        return ResponseEntity.ok(usecases);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UseCase> updateCommentAndUrl(@PathVariable Integer id, @RequestBody UseCase entity) {
        UseCase useCase = useCaseService.addCommentAndUrl(id, entity);
        return ResponseEntity.ok(useCase);
    }
    
}
