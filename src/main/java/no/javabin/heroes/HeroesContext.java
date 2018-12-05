package no.javabin.heroes;

import java.io.IOException;

import no.javabin.infrastructure.configuration.ApplicationProperties;
import no.javabin.infrastructure.http.HttpUrl;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

public class HeroesContext implements ProfileContext {

    private final ApplicationProperties property;

    public HeroesContext(ApplicationProperties propertySource) {
        this.property = propertySource;
    }

    @Override
    public HttpUrl createAuthorizationUrl(String state) {
        return new HttpUrl("https://slack.com/oauth/authorize")
                .addParameter("client_id", getClientId())
                .addParameter("redirect_uri", getRedirectUri())
                .addParameter("state", state)
                .addParameter("scope", "identity.basic,identity.email");
    }

    @Override
    public JsonObject exchangeCodeForToken(String code) throws IOException {
        HttpUrl tokenRequest = new HttpUrl("https://slack.com/api/oauth.access")
                .addParameter("client_id", getClientId())
                .addParameter("client_secret", getClientSecret())
                .addParameter("code", code);
        return JsonParser.parseToObject(tokenRequest.toURL());
    }

    public String getRedirectUri() {
        return property.required("oauth2.redirect_uri");
    }

    public String getClientId() {
        return property.required("oauth2.client_id");
    }

    public String getClientSecret() {
        return property.required("oauth2.client_secret");
    }
}
