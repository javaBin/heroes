package no.javabin.heroes.person;

import no.javabin.heroes.NotFoundException;
import no.javabin.heroes.TestDataUtil;
import no.javabin.heroes.exception.ValidationException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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

  @Test
  public void shouldReturnAllPersons() throws Exception {
    when(dao.getAllPersons()).thenReturn(Arrays.asList(TestDataUtil.buildDefaultPerson()));
    JsonArray allPersons = service.getAllPersons();
    assertThat(allPersons.size(), is(1));
  }

  @Test
  public void shouldReturnHeroById() throws Exception {
    String fakeId = "12345";
    when(dao.getPersonById(fakeId)).thenReturn(Optional.of(TestDataUtil.buildDefaultPersonWithId(fakeId)));
    JsonObject hero = service.getPersonById(fakeId);
    assertThat(hero, notNullValue());
  }

  @Test
  public void shouldReturnHeroByEmail() throws Exception {
    when(dao.getPersonsByEmail(anyString())).thenReturn(Arrays.asList(TestDataUtil.buildDefaultPerson()));
    JsonArray heroes = service.getPersonByEmail("an.email@com");
    assertThat(heroes.size(), is(1));
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowExceptionWhenHeroNotFoundByEmail() throws Exception {
    when(dao.getPersonsByEmail(anyString())).thenReturn(Collections.emptyList());
    service.getPersonByEmail("an.email@com");
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowExceptionWhenInvalidId() throws Exception {
    String fakeId = "12345";
    when(dao.getPersonById(fakeId)).thenReturn(Optional.empty());
    JsonObject hero = service.getPersonById(fakeId);
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
    when(dao.insertPerson(any())).thenReturn(TestDataUtil.buildDefaultPerson());
    service.insertPerson(person);
    verify(dao).insertPerson(any());
  }


}