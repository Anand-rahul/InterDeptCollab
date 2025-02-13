package com.sharktank.interdepcollab.solution.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sharktank.interdepcollab.solution.model.Solution;

public interface SolutionRepository extends JpaRepository<Solution, Integer> {
}