package com.sharktank.interdepcollab.ai.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharktank.interdepcollab.ai.Model.Optimax;

@Repository
public interface OptimaxRepository extends JpaRepository<Optimax,Long>{
    
}
