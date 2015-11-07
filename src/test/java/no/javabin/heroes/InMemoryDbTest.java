package no.javabin.heroes;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;

public class InMemoryDbTest {
    private static InMemoryDb inMemoryDb;
    private ServiceLocator serviceLocator;

    @BeforeClass
    public static void createDb() {
        inMemoryDb = new InMemoryDb();
    }

    @Before
    public void startTransaction() throws Exception {
        Connection connection = inMemoryDb.connection();
        serviceLocator = ServiceLocator.startThreadContext();
        serviceLocator.setConnection(connection);
    }

    @After
    public void rollbackAndClose() throws Exception {
        serviceLocator.connection().rollback();
        serviceLocator.close();


    }
}
