package com.sharktank.interdepcollab.ai.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sharktank.interdepcollab.ai.Model.VectorStore;


@Repository
public interface VectorRepository extends JpaRepository<VectorStore, Long> {


    // @NativeQuery(value = "SELECT v FROM vectors v WHERE v.source_type = :sourceType LIMIT :topK")//ORDER BY embedding <=> CAST(:queryVector AS vector)
    // List<VectorStore> searchByCosineSimilarity(
    //         @Param("queryVector") String queryVector,
    //         @Param("sourceType") String sourceType,
    //         @Param("topK") int topK
    // );

    // @NativeQuery(value = "SELECT v.id, v.source_type,v.source_id,v.text,v.json_data,v.embedding FROM vectors v WHERE v.source_type = ? LIMIT ?")//ORDER BY embedding <=> CAST(:queryVector AS vector)
    // List<VectorStore> searchByCosineSimilarity(
    //         @Param("sourceType") String sourceType,
    //         @Param("topK") int topK
    // );

    // @Query(value = "SELECT v.id, v.text, v.source_id, v.source_type FROM vectors v WHERE v.source_type = ?1 LIMIT ?2", nativeQuery = true)
    // List<VectorStore> searchByCosineSimilarity(String sourceType, int topK);

    // @Query(value = "SELECT v.text,v.json_data,v.source_id FROM vectors v WHERE v.source_type = ?1 LIMIT ?2", nativeQuery = true)
    // List<Object[]> findIdsBySourceType(String sourceType, int topK);

    @Query(value="SELECT v.text, v.json_data, CAST(v.source_id AS VARCHAR) FROM vectors v WHERE v.source_type = ?1AND (v.json_data::jsonb ->> 'sourceId') = ?4 ORDER BY embedding <=> CAST(?2 AS vector) LIMIT ?3",nativeQuery = true)
    List<Object[]> searchSolutionByCosineSimilarity(String sourceType,String queryVector,int topK,String sourceId);

    @Query(value = "SELECT v.text, v.json_data, CAST(v.source_id AS VARCHAR) FROM vectors v WHERE v.source_type = ?1 and v.id>3 ORDER BY embedding <=> CAST(?2 AS vector) LIMIT ?3", nativeQuery = true)
    List<Object[]> searchByCosineSimilarity(String sourceType,String queryVector, int topK);

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