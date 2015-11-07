package no.javabin.heroes;

import org.flywaydb.core.Flyway;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class InMemoryDb {
    private final JDBCDataSource jdbcDataSource;

    public InMemoryDb() {
        jdbcDataSource = new JDBCDataSource();
        jdbcDataSource.setUrl("jdbc:hsqldb:mem:test");
        jdbcDataSource.setUser("SA");
        jdbcDataSource.setPassword("");
        migrateDb(jdbcDataSource);
    }

    private void migrateDb(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }


    public Connection connection() {
        try {
            return jdbcDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
