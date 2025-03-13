package com.sharktank.interdepcollab.solution.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharktank.interdepcollab.solution.model.UseCase;

public interface UseCaseRepository extends JpaRepository<UseCase, Integer> {
    
}
