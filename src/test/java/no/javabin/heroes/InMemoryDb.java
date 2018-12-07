package no.javabin.heroes;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class InMemoryDb {
  private final JdbcDataSource jdbcDataSource;

  public InMemoryDb() throws SQLException {
      jdbcDataSource = new JdbcDataSource();
      jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
      Flyway.configure().dataSource(jdbcDataSource).load().migrate();
  }

  public Connection connection() {
    try {
      return jdbcDataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
