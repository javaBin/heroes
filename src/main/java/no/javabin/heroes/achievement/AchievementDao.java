package no.javabin.heroes.achievement;

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

public class AchievementDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(AchievementDao.class);

  public Optional<JsonObject> getAchievementById(String id) {
    ServiceLocator locator = ServiceLocator.instance();
    Connection connection = locator.connection();
    try (PreparedStatement statement = connection.prepareStatement("SELECT data FROM achievement WHERE id = ?")) {
      statement.setString(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (!resultSet.next()) {
        throw new NotFoundException("Did not find achievement with id: " + id);
      }
      String data = resultSet.getString(1);
      LOGGER.debug("Found achievement {} with id {}", data, id);
      return Optional.of(JsonParser.parseToObject(data));
    } catch (SQLException e) {
      LOGGER.error("Error getting achievement with id {}.", id, e);
      throw new DatabaseException("Error getting achievement with id " + id);
    }
  }


  public List<JsonObject> getAllAchievements() {
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


  public JsonObject insertAchievement(final JsonObject anAchievement) {
    ServiceLocator locator = ServiceLocator.instance();
    Connection connection = locator.connection();
    JsonObject newAchievement = anAchievement.deepClone();
    String id = UUID.randomUUID().toString();
    newAchievement.put("id", id);
    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO achievement(id, data) VALUES (?,?)")) {
      statement.setString(1, id);
      statement.setString(2, newAchievement.toJson());
      statement.executeUpdate();
    } catch (SQLException e) {
      LOGGER.error("Error inserting achievement {} into database.", newAchievement, e);
      throw new DatabaseException("Error inserting achievement into database");
    }
    return newAchievement;
  }
}
