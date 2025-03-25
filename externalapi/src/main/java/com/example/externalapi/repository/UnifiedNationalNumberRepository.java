package com.example.externalapi.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
public class UnifiedNationalNumberRepository {
    private static final java.util.logging.Logger logger = Logger.getLogger(UnifiedNationalNumberRepository.class.getName());

    @PersistenceContext
    private final EntityManager entityManager;

    public UnifiedNationalNumberRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<String> getUnifiedNationalNumbers() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("GetUnifiedNationalNumbers");
        logger.info("executing procedure GetUnifiedNationalNumbers");

        List<?> resultList = query.getResultList();
        return resultList.stream().map(Object::toString).collect(Collectors.toList());
    }
}
