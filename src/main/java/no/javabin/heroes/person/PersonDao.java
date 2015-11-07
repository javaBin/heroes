package no.javabin.heroes.person;

import no.javabin.heroes.ServiceLocator;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;

import java.sql.Connection;
import java.util.Optional;

public class PersonDao {
  public Optional<JsonObject> getPersonById(String id){
    ServiceLocator locator = ServiceLocator.instance();
      Connection connection = locator.connection();
      return null;
  }
}
