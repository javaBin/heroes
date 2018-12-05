package no.javabin.heroes;

import java.io.IOException;

import org.jsonbuddy.JsonObject;

public interface Oauth2Configuration {

    HttpUrl createAuthorizationUrl(String state);

    JsonObject exchangeCodeForToken(String code) throws IOException;

}
