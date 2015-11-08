package no.javabin.heroes.person;

import no.javabin.heroes.ServiceLocator;
import no.javabin.heroes.exception.DatabaseException;
import org.jsonbuddy.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class PersonDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonDao.class);

  public Optional<JsonObject> getPersonById(String id) {
    ServiceLocator locator = ServiceLocator.instance();
    Connection connection = locator.connection();
    return null;
  }


  public JsonObject insertPerson(final JsonObject aPerson) {
    ServiceLocator locator = ServiceLocator.instance();
    Connection connection = locator.connection();
    JsonObject newPerson = aPerson.deepClone();
    String id = UUID.randomUUID().toString();
    newPerson.put("id", id);
    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO person(id, data) VALUES (?,?)")) {
      statement.setString(1, id);
      statement.setString(2, newPerson.toJson());
      statement.executeUpdate();
    } catch (SQLException e) {
      LOGGER.error("Error inserting person {} into database.", newPerson, e);
      throw new DatabaseException("Error inserting person into database");
    }
    return newPerson;
  }
}
