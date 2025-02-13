package com.sharktank.interdepcollab.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sharktank.interdepcollab.resource.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Integer> {
}