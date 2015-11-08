package no.javabin.heroes.person;

import no.javabin.heroes.NotFoundException;
import no.javabin.heroes.ServiceLocator;
import no.javabin.heroes.exception.DatabaseException;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PersonDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersonDao.class);

  public Optional<JsonObject> getPersonById(String id) {
    ServiceLocator locator = ServiceLocator.instance();
    Connection connection = locator.connection();
    try (PreparedStatement statement = connection.prepareStatement("SELECT data FROM person WHERE id = ?")) {
      statement.setString(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        throw new NotFoundException("Did not find person with id: " + id);
      }
      String data = resultSet.getString(1);
      LOGGER.debug("Found person {} with id {}", data, id);
      return Optional.of(JsonParser.parseToObject(data));
    } catch (SQLException e) {
      LOGGER.error("Error getting person with id {}.", id, e);
      throw new DatabaseException("Error getting person with id " + id);
    }
  }


  public List<JsonObject> getAllPersons() {
    List<JsonObject> heroes = new ArrayList<>();
    ServiceLocator locator = ServiceLocator.instance();
    Connection connection = locator.connection();
    try (PreparedStatement statement = connection.prepareStatement("SELECT data FROM person")) {
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        heroes.add(JsonParser.parseToObject(resultSet.getString(1)));
      }
    } catch (SQLException e) {
      LOGGER.error("Error getting all heroes from database.", e);
      throw new DatabaseException("Error getting all heroes from database.");
    }
    return heroes;
  }

  public List<JsonObject> getPersonsByEmail(final String email) {
    return getAllPersons().stream()
        .filter(p -> p.stringValue("email").isPresent())
        .filter(p -> p.stringValue("email").get().equalsIgnoreCase(email))
        .collect(Collectors.toList());
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
