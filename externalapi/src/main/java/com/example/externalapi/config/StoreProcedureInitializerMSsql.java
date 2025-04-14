package com.example.externalapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.logging.Logger;

@Component
@Profile("qa")
public class StoreProcedureInitializerMSsql implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(StoreProcedureInitializerMSsql.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        String checkProcSql = """
                SELECT COUNT(*) FROM sys.objects
                WHERE type = 'P' AND name = 'GetUnifiedNationalNumbers'
                """;

        Integer count = jdbcTemplate.queryForObject(checkProcSql, Integer.class);

        if (count != null && count == 0) {
            String createProcSql = """
                    CREATE PROCEDURE GetUnifiedNationalNumbers
                    AS
                    BEGIN
                        SELECT '7009004321' AS UnifiedNationalNumber
                        UNION SELECT '7009004320'
                        UNION SELECT '7009004322'
                        UNION SELECT '7009004323'
                        UNION SELECT '7009004324'
                        UNION SELECT '7001781587';
                    END
                    """;

            jdbcTemplate.execute(createProcSql);
            logger.info("Stored procedure created in MS sql database.");
        } else {
            logger.info("Stored procedure already exists in MS sql database.");
        }
    }
}

