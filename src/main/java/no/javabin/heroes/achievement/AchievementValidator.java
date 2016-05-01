package no.javabin.heroes.achievement;

import org.jsonbuddy.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AchievementValidator {
  private JsonObject person;

  public AchievementValidator(JsonObject person) {
    this.person = person;
  }

  public List<String> validateCreate() {
    List<String> errors = checkRequiredFields("name", "description");
    return errors;
  }

  private List<String> checkRequiredFields(String... fields) {
    List<String> errors = new ArrayList<>();
    for (String field : fields) {
      Optional<String> value = person.stringValue(field);
      if (!value.isPresent()) {
        errors.add("Required field '" + field + "' is missing.");
      } else {
        if (value.get().isEmpty()) {
          errors.add("Required field '" + field + "' is empty");
        }
      }
    }
    return errors;
  }
}