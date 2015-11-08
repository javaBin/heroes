package no.javabin.heroes.person;

import no.javabin.heroes.NotFoundException;
import no.javabin.heroes.TestDataUtil;
import no.javabin.heroes.exception.ValidationException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jsonbuddy.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PersonServiceTest {
  private PersonService service;
  private PersonDao dao;
  private JsonObject person;

  @Before
  public void setUp() throws Exception {
    dao = mock(PersonDao.class);
    service = new PersonService(dao);
    person = TestDataUtil.buildDefaultPerson();
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowExceptionWhenNotFound() throws Exception {
    when(dao.getPersonById(anyString())).thenReturn(Optional.empty());
    service.getPersonById("AnId");
  }

  @Test(expected = ValidationException.class)
  public void insertDhouldThrowExceptionWhenValidationFailes() throws Exception {
    person.remove("name");
    service.insertPerson(person);
  }

  @Test(expected = ValidationException.class)
  public void insertShouldThrowExceptionWhenValidationFailes() throws Exception {
    person.put("name", "");
    service.insertPerson(person);
  }

  @Test
  public void insertShouldWork() throws Exception {
    JsonObject newPerson = service.insertPerson(person);
    when(dao.insertPerson(any())).thenReturn(TestDataUtil.buildDefaultPerson());
    verify(dao).insertPerson(any());
  }


}