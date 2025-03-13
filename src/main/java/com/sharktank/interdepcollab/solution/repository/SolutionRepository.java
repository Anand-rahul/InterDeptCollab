package com.sharktank.interdepcollab.solution.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sharktank.interdepcollab.solution.model.Solution;

public interface SolutionRepository extends JpaRepository<Solution, Integer> {

    @Query(value = "SELECT id FROM solution WHERE title LIKE CONCAT('%', ?1, '%')", nativeQuery = true)
    List<String> getAllSolutionsLike(@Param("paramToBeChecked")String paramToBeChecked);
}