package com.sharktank.interdepcollab.ai.Repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class VectorStoreRepository{

    @PersistenceContext
    public EntityManager entityManager;

}