package com.example.externalapi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class UnifiedNationalNumberRepository {

    private static final Logger logger = Logger.getLogger(UnifiedNationalNumberRepository.class.getName());

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UnifiedNationalNumberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getUnifiedNationalNumbers(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            logger.warning("userId is null or empty. Returning empty list.");
            return Collections.emptyList();
        }

        String sql = "EXEC Establishments_Get_All_User_Type_QIWA_AS_A_Service @p_IDNumber = ?";
        List<String> list = new ArrayList<>();

        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);

            while (rowSet.next()) {
                String nationalUnifiedNumber = rowSet.getString("NationalUnifiedNumber");
                if (StringUtils.hasText(nationalUnifiedNumber)) {
                    list.add(nationalUnifiedNumber);
                }
            }

            logger.info("Fetched " + list.size() + " national unified number(s) for userId: " + userId);

        } catch (Exception ex) {
            logger.severe("Error executing stored procedure: " + ex.getMessage());
            throw new RuntimeException("Failed to fetch unified national numbers", ex);
        }

        return list;
    }
}
