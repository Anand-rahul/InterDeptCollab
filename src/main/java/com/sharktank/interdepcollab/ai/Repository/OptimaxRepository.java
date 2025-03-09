package com.sharktank.interdepcollab.ai.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sharktank.interdepcollab.ai.Model.Optimax;

@Repository
public interface OptimaxRepository extends JpaRepository<Optimax,Long>{
    

    @Query(value="SELECT o.id, o.sol_id1, o.sol_id2,o.general_similarity_score FROM Optimax o WHERE o.general_similarity_score > :threshold",nativeQuery=true)
    List<Object[]> findSimilarSolutionsAboveThreshold(@Param("threshold") double threshold);
}
