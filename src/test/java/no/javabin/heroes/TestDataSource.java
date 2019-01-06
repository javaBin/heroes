package no.javabin.heroes;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

public class TestDataSource {

    private static DataSource dataSource;

    public static DataSource createDataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource = jdbcDataSource;
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

}
