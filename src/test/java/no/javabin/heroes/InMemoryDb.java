package no.javabin.heroes;

import no.javabin.heroes.person.PersonDao;
import org.flywaydb.core.Flyway;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class InMemoryDb {
  private final JDBCDataSource jdbcDataSource;

  public InMemoryDb() throws SQLException {
    jdbcDataSource = new JDBCDataSource();
    jdbcDataSource.setUrl("jdbc:hsqldb:mem:test;MODE=PostgreSQL");
    jdbcDataSource.setUser("SA");
    jdbcDataSource.setPassword("");
    migrateDb(jdbcDataSource);
    addTestData();
  }

  private void addTestData() throws SQLException {
    ServiceLocator serviceLocator = ServiceLocator.startThreadContext();
    serviceLocator.setConnection(jdbcDataSource.getConnection());
    insertPersonData();
    serviceLocator.close();
  }

  private static void insertPersonData() {
    PersonDao dao = new PersonDao();
    dao.insertPerson(TestDataUtil.buildPerson("Test Person", "email@mail.com", "+47 12345678", Optional.of("Bergen")));
  }


  private void migrateDb(DataSource dataSource) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.clean();
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
