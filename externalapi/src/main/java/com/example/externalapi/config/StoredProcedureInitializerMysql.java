package com.example.externalapi.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;


@Component
@Profile("dev")
public class StoredProcedureInitializerMysql implements CommandLineRunner {

    private static final java.util.logging.Logger logger = Logger.getLogger(StoredProcedureInitializerMysql.class.getName());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        String checkProcSql = "SELECT COUNT(*) FROM information_schema.routines " +
                "WHERE routine_schema = 'temp' AND routine_name = 'GetUnifiedNationalNumbers' AND routine_type='PROCEDURE'";

        Integer count = jdbcTemplate.queryForObject(checkProcSql, Integer.class);

        if (count != null && count == 0) {
            String createProcSql = """
                    DELIMITER //
                    CREATE PROCEDURE GetUnifiedNationalNumbers()
                    BEGIN
                        SELECT '7009004321' AS UnifiedNationalNumber
                        UNION SELECT '7009004320'
                        UNION SELECT '7009004322'
                        UNION SELECT '7009004323'
                        UNION SELECT '7009004324'
                        UNION SELECT '7001781587';
                    END //
                    DELIMITER ;
                    """;

            // Split and execute without DELIMITER lines (not supported via JDBC)
            jdbcTemplate.execute("""
                        CREATE PROCEDURE GetUnifiedNationalNumbers()
                        BEGIN
                            SELECT '7009004321' AS UnifiedNationalNumber
                            UNION SELECT '7009004320'
                            UNION SELECT '7009004322'
                            UNION SELECT '7009004323'
                            UNION SELECT '7009004324'
                            UNION SELECT '7001781587';
                        END
                    """);

            logger.info("Stored procedure created in mysql database.");
        } else {
            logger.info("Stored procedure already exists in mysql database.");
        }
    }
}
