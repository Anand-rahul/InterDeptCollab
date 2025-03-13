package com.sharktank.interdepcollab.requirement.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sharktank.interdepcollab.requirement.model.Requirement;
import com.sharktank.interdepcollab.requirement.model.Status;
import com.sharktank.interdepcollab.user.model.AppUser;

public interface RequirementRepository extends JpaRepository<Requirement, Integer> {
    Page<Requirement> findBySolutionIsNullAndStatus(Status status, Pageable pageable);
    Page<Requirement> findByCreatedByOrAssignedTo(AppUser creator, AppUser assigned, Pageable pageable);
}