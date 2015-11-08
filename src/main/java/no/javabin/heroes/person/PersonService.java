package no.javabin.heroes.person;

import no.javabin.heroes.NotFoundException;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

import java.util.List;
import java.util.Optional;

public class PersonService {
  private PersonDao dao;

  public PersonService(PersonDao dao) {
    this.dao = dao;
  }

  public JsonObject getPersonById(String id) {
    Optional<JsonObject> person = dao.getPersonById(id);
    return person.orElseThrow(() -> new NotFoundException("Did not find person with id " + id));
  }

  public JsonArray getAllPersons() {
    return new JsonArray();
  }

  public JsonObject insertPerson(JsonObject person) {
    List<String> validationErrors = new PersonValidator(person).validateCreate();
    if (validationErrors.isEmpty()) {
      return dao.insertPerson(person);
    } else {
      throw new no.javabin.heroes.exception.ValidationException("Validation of person failed.", validationErrors);
    }
  }

  public void setDao(PersonDao dao) {
    this.dao = dao;
  }
}
