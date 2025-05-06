package com.org.api.repository;

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
public class UnifiedNationalNumberFetcherRepository {

    private static final Logger logger = Logger.getLogger(UnifiedNationalNumberFetcherRepository.class.getName());

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UnifiedNationalNumberFetcherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> fetchUnifiedNationalNumbers(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            logger.warning("userId is null or empty. Returning empty list.");
            return Collections.emptyList();
        }

        String sql = "EXEC Establishments_Get_All_User_Type_QIWA_AS_A_Service " +
                "@p_IDNumber = ?, " +
                "@p_UserSubScriptions = ?, " +
                "@p_IsExcludeEconomicActivity = ?, " +
                "@p_EstablishmentName = ?, " +
                "@p_UnifiedNumber = ?, " +
                "@p_LaborOfficeId = ?, " +
                "@p_SequenceNumber = ?";
        List<String> list = new ArrayList<>();

        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, null, null, null, null, null, null);

            while (rowSet.next()) {
                String nationalUnifiedNumber = rowSet.getString("NationalUnifiedNumber");
                if (StringUtils.hasText(nationalUnifiedNumber)) {
                    list.add(nationalUnifiedNumber);
                }
            }

            logger.info("Fetched " + list.size() + " national unified number(s) for userId: " + userId);

        } catch (Exception ex) {
            logger.severe("An Error occurred while executing stored procedure");
            throw new RuntimeException("Failed to fetch unified national numbers", ex);
        }

        return list;
    }
}
