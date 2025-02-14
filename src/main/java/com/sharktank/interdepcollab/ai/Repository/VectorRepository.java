package com.sharktank.interdepcollab.ai.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sharktank.interdepcollab.ai.Model.VectorStore;


@Repository
public interface VectorRepository extends JpaRepository<VectorStore, Long> {


    @Query(value = """
        SELECT * FROM vectors
        WHERE (:sourceType IS NULL OR source_type = :sourceType)
        ORDER BY embedding <=> CAST(:queryVector AS vector)
        LIMIT :topK
    """, nativeQuery = true)
    List<VectorStore> searchByCosineSimilarity(
            @Param("queryVector") String queryVector,
            @Param("sourceType") String sourceType,
            @Param("topK") int topK
    );

    @Query(value = """
        SELECT * FROM vectors
        WHERE (:sourceType IS NULL OR source_type = :sourceType)
        ORDER BY embedding <-> CAST(:queryVector AS vector)
        LIMIT :topK
    """, nativeQuery = true)
    List<VectorStore> searchByL2Distance(
            @Param("queryVector") String queryVector,
            @Param("sourceType") String sourceType,
            @Param("topK") int topK
    );
}