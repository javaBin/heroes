package no.javabin.heroes;

import org.jsonbuddy.JsonObject;

import java.util.Optional;

public class TestDataUtil {

  public static JsonObject buildDefaultPerson() {
    return TestDataUtil.buildPerson("Name", "Email", "Phone", Optional.of("City"));
  }

  public static JsonObject buildDefaultPersonWithId(String id) {
    JsonObject hero = TestDataUtil.buildPerson("Name", "Email", "Phone", Optional.of("City"));
    hero.put("id", id);
    return hero;
  }

  public static JsonObject buildPerson(String name, String email, String phone, Optional<String> city) {
    JsonObject p = new JsonObject();
    p.put("name", name);
    p.put("email", email);
    p.put("phone", phone);
    city.ifPresent(c -> p.put("city", c));
    return p;
  }
}
