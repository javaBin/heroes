package no.javabin.heroes;

import no.javabin.heroes.person.PersonDao;
import org.jsonbuddy.JsonObject;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class PersonDaoTest extends InMemoryDbTest {

  @Test
  public void insertedPersonShouldHaveId() throws Exception {
    PersonDao dao = new PersonDao();
    JsonObject person = dao.insertPerson(TestDataUtil.buildPerson("New P. Erson", "p@mail.com", "1234", Optional.empty()));
    assertTrue(person.stringValue("id").isPresent());

  }
}
