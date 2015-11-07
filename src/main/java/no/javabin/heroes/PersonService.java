package no.javabin.heroes;

import no.javabin.heroes.person.PersonDao;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;

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

  public JsonArray getAllPersons(){
    return new JsonArray();
  }

  public JsonObject addPerson(JsonObject person){
    return new JsonObject();
  }

  public void setDao(PersonDao dao) {
    this.dao = dao;
  }
}
