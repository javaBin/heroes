package no.javabin.heroes.person;

import no.javabin.heroes.InMemoryDbTest;
import no.javabin.heroes.TestDataUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.jsonbuddy.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PersonDaoTest extends InMemoryDbTest {

  @Test
  public void insertedPersonShouldHaveId() throws Exception {
    PersonDao dao = new PersonDao();
    JsonObject person = dao.insertPerson(TestDataUtil.buildPerson("New P. Erson", "p@mail.com", "1234", Optional.empty()));
    Assert.assertTrue(person.stringValue("id").isPresent());
  }

  @Test
  public void getAllPersons() throws Exception {
    PersonDao dao = new PersonDao();
    JsonObject person = dao.insertPerson(TestDataUtil.buildPerson("New P. Erson", "p@mail.com", "1234", Optional.empty()));
    Assert.assertTrue(person.stringValue("id").isPresent());
  }

  @Test
  public void deepCloneShouldCopyWholeObject() throws Exception {
    JsonObject origin = TestDataUtil.buildPerson("Name", "Email", "Phone", Optional.of("City"));
    JsonObject result = origin.deepClone();
    assertThat(result.stringValue("name").get(), is("Name"));
    assertThat(result.stringValue("email").get(), is("Email"));
    assertThat(result.stringValue("phone").get(), is("Phone"));
    assertThat(result.stringValue("city").get(), is("City"));
  }
}
