package com.sharktank.interdepcollab.requirement.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sharktank.interdepcollab.requirement.model.Requirement;

public interface RequirementRepository extends JpaRepository<Requirement, Integer> {
}