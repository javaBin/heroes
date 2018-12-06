package no.javabin.heroes;

import java.io.IOException;
import java.util.List;

import org.jsonbuddy.JsonObject;

public interface Profile {

    String getUsername();

    boolean isAdmin();

    List<JsonObject> listUsers() throws IOException;

}
