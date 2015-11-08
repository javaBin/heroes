package no.javabin.heroes;

import no.javabin.heroes.person.PersonDao;
import org.jsonbuddy.JsonObject;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

public class InsertInitialDataTest extends InMemoryDbTest {
  @Test
  @Ignore
  public void testPersonsInserted() throws Exception {
    try (Statement statement = ServiceLocator.instance().connection().createStatement()) {
      ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM person");
      resultSet.next();
      assertThat(resultSet.getInt(1), is(1));
    }

  }

  @Test
  @Ignore
  public void insertedPersonShouldHaveId() throws Exception {
    PersonDao dao = new PersonDao();
    JsonObject person = dao.insertPerson(TestDataUtil.buildPerson("New P. Erson", "p@mail.com", "1234", Optional.empty()));
    assertTrue(person.stringValue("id").isPresent());

  }

}
