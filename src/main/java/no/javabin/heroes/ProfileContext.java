package no.javabin.heroes;

import java.io.IOException;

import no.javabin.infrastructure.http.HttpUrl;
import org.jsonbuddy.JsonObject;

public interface ProfileContext {

    HttpUrl createAuthorizationUrl(String state);

    JsonObject exchangeCodeForToken(String code) throws IOException;

}
