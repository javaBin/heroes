package no.javabin.heroes;

import java.io.IOException;

import no.javabin.infrastructure.configuration.ApplicationProperties;
import no.javabin.infrastructure.http.HttpUrl;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeroesContextSlack implements ProfileContext {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final ApplicationProperties property;

    public HeroesContextSlack(ApplicationProperties propertySource) {
        this.property = propertySource;
    }

    @Override
    public HttpUrl createAuthorizationUrl(String state) {
        return new HttpUrl("https://slack.com/oauth/authorize")
                .addParameter("client_id", getClientId())
                .addParameter("redirect_uri", getRedirectUri())
                .addParameter("team", "TEM1Z3KKN")
                .addParameter("state", state)
                .addParameter("scope", "groups:read,channels:read,users.profile:read,users:read,users:read.email");
        // users:read and users:read.email required only for admins
    }

    private JsonObject exchangeCodeForToken(String code) throws IOException {
        String tokenEndpoint = "https://slack.com/api/oauth.access";
        HttpUrl tokenRequest = new HttpUrl(tokenEndpoint)
                .addParameter("client_id", getClientId())
                .addParameter("client_secret", getClientSecret())
                .addParameter("code", code);
        logger.debug("Fetching profile from {}", tokenEndpoint);
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

    @Override
    public Profile exchangeCodeForProfile(String code) throws IOException {
        JsonObject tokenResponse = exchangeCodeForToken(code);
        if (tokenResponse.containsKey("error")) {
            logger.error("Token request failed: {}", tokenResponse);
            throw new HttpRequestException(500, "Failed to authenticate client");
        }


        return new SlackProfile(tokenResponse);
    }
}
