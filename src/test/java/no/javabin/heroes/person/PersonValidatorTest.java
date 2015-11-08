package no.javabin.heroes.person;

import no.javabin.heroes.TestDataUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.jsonbuddy.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PersonValidatorTest {

  private JsonObject jsonObject;
  private PersonValidator validator;

  @Before
  public void setUp() throws Exception {
    jsonObject = TestDataUtil.buildPerson("Name", "Email", "Phone", Optional.empty());
  }

  @Test
  public void missingRequiredFieldsShouldGiveError() throws Exception {
    JsonObject person = jsonObject;
    validator = new PersonValidator(person);
    person.remove("name");
    List<String> errors = validator.validateCreate();
    assertThat(errors.size(), is(1));
    String errorMessage = errors.get(0);
    assertThat(errorMessage, containsString("is missing"));
    assertThat(errorMessage, containsString("name"));
  }

  @Test
  public void emptyRequiredFieldsShouldGiveError() throws Exception {
    JsonObject person = jsonObject;
    validator = new PersonValidator(person);
    person.put("name", "");
    List<String> errors = validator.validateCreate();
    assertThat(errors.size(), is(1));
    String errorMessage = errors.get(0);
    assertThat(errorMessage, containsString("is empty"));
    assertThat(errorMessage, containsString("name"));
  }
}