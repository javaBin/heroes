package no.javabin.heroes;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.SQLException;

public class InMemoryDbTest {
  private InMemoryDb inMemoryDb;
  private ServiceLocator serviceLocator;


  @Before
  public void startTransaction() throws Exception {
    inMemoryDb = new InMemoryDb();
    Connection connection = inMemoryDb.connection();
    serviceLocator = ServiceLocator.startThreadContext();
    serviceLocator.setConnection(connection);
  }

  @After
  public void rollbackAndClose() throws Exception {
    serviceLocator.close();


  }

}
