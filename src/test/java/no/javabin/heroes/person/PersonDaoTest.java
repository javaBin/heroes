package no.javabin.heroes.person;

import no.javabin.heroes.InMemoryDbTest;
import no.javabin.heroes.NotFoundException;
import no.javabin.heroes.TestDataUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.jsonbuddy.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;

public class PersonDaoTest extends InMemoryDbTest {

  @Test
  public void insertedPersonShouldHaveId() throws Exception {
    PersonDao dao = new PersonDao();
    JsonObject person = dao.insertPerson(TestDataUtil.buildPerson("New P. Erson", "p@mail.com", "1234", Optional.empty()));
    assertTrue(person.stringValue("id").isPresent());
  }

  @Test
  public void getAllPersons() throws Exception {
    PersonDao dao = new PersonDao();
    List<JsonObject> allPersons = dao.getAllPersons();
    Assert.assertThat(allPersons.size(), is(1));
  }

  @Test
  public void getHeroByEmailThatExists() throws Exception {
    PersonDao dao = new PersonDao();
    List<JsonObject> allPersons = dao.getPersonsByEmail("email@mail.com");
    Assert.assertThat(allPersons.size(), is(1));
  }

  @Test
  public void getHeroByEmailThatDoesNotExists() throws Exception {
    PersonDao dao = new PersonDao();
    List<JsonObject> allPersons = dao.getPersonsByEmail("no.email");
    Assert.assertThat(allPersons.size(), is(0));
  }

  @Test
  public void getHeroById() throws Exception {
    PersonDao dao = new PersonDao();
    JsonObject addedHero = dao.insertPerson(TestDataUtil.buildDefaultPerson());
    assertTrue(addedHero.stringValue("id").isPresent());
    Optional<JsonObject> heroById = dao.getPersonById(addedHero.stringValue("id").get());
    assertTrue(heroById.isPresent());
  }

  @Test(expected = NotFoundException.class)
  public void getHeroByInvalidIdExpectsException() throws Exception {
    PersonDao dao = new PersonDao();
    Optional<JsonObject> heroById = dao.getPersonById("Fake ID");
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
