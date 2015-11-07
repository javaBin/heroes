package no.javabin.heroes;

import org.hsqldb.jdbc.JDBCDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class InMemoryDb {
    private final JDBCDataSource jdbcDataSource;

    public InMemoryDb() {
        jdbcDataSource = new JDBCDataSource();
        jdbcDataSource.setUrl("jdbc:hsqldb:mem:test");
        jdbcDataSource.setUser("SA");
        jdbcDataSource.setPassword("");
    }

    public Connection connection() {
        try {
            return jdbcDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
