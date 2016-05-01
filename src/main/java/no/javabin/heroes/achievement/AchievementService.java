package no.javabin.heroes.achievement;

import no.javabin.heroes.NotFoundException;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

import java.util.List;
import java.util.Optional;

public class AchievementService {
  private AchievementDao dao;

  public AchievementService(AchievementDao dao) {
    this.dao = dao;
  }

  public JsonObject getAchievementById(String id) {
    Optional<JsonObject> person = dao.getAchievementById(id);
    return person.orElseThrow(() -> new NotFoundException("Did not find person with id " + id));
  }

  public JsonArray getAllAvhievements() {
    List<JsonObject> allPersons = dao.getAllAchievements();
    return JsonArray.fromNodeList(allPersons);
  }

  public JsonObject insertAchievement(JsonObject achievement) {
    List<String> validationErrors = new AchievementValidator(achievement).validateCreate();
    if (validationErrors.isEmpty()) {
      return dao.insertAchievement(achievement);
    } else {
      throw new no.javabin.heroes.exception.ValidationException("Validation of achievements failed.", validationErrors);
    }
  }

  public void setDao(AchievementDao dao) {
    this.dao = dao;
  }
}
