package no.javabin.heroes.slack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.javabin.heroes.Profile;
import no.javabin.infrastructure.ExceptionUtil;
import no.javabin.infrastructure.http.HttpUrl;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

public class SlackProfile implements Profile {

    private boolean hasAdminScope;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getTwitterHandle() {
        return this.twitterHandle.orElse(null);
    }

    @Override
    public boolean isAdmin() {
        try {
            JsonObject conversations = slackJsonGet(accessToken, "conversations.list");
            // id: CEN9Z1E23
            admin = conversations.requiredArray("channels")
                    .objectStream()
                    .filter(channel -> channel.requiredBoolean("is_member"))
                    .map(channel -> channel.requiredString("name"))
                    .anyMatch(s -> s.equals("admin"));
            return admin;
        } catch (IOException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    @Override
    public boolean hasAdminScope() {
        return hasAdminScope;
    }

    @Override
    public List<JsonObject> listUsers() throws IOException {
        JsonObject userList = slackJsonGet(accessToken, "users.list");
        return userList
                .requiredArray("members")
                .objectStream()
                .filter(o -> !o.booleanValue("is_bot").orElse(false))
                .map(o -> o.requiredObject("profile"))
                .filter(o -> o.containsKey("email"))
                .map(o -> new JsonObject()
                        .put("name", o.requiredString("real_name"))
                        .put("email", o.requiredString("email"))
                )
                .collect(Collectors.toList());
    }

    private String username;

    private boolean admin;

    private String accessToken;

    private String email;

    private Optional<String> twitterHandle;

    public SlackProfile(JsonObject tokenResponse) throws IOException {
        this.accessToken = tokenResponse.requiredString("access_token");

        List<String> scopes = Arrays.asList(tokenResponse.requiredString("scope").split(","));
        this.hasAdminScope = scopes.contains("users:read.email");

        JsonObject userProfile = slackJsonGet(accessToken, "users.profile.get");
        this.username = userProfile.requiredObject("profile").requiredString("real_name");
        this.email = userProfile.requiredObject("profile").requiredString("email");
        this.twitterHandle = userProfile.requiredObject("profile").stringValue("twitter");

        JsonObject conversations = slackJsonGet(accessToken, "conversations.list");
        // id: CEN9Z1E23
        admin = conversations.requiredArray("channels")
                .objectStream()
                .filter(channel -> channel.requiredBoolean("is_member"))
                .map(channel -> channel.requiredString("name"))
                .anyMatch(s -> s.equals("admin"));
    }

    public JsonObject slackJsonGet() throws IOException {
        return slackJsonGet(accessToken, "conversations.list");
    }

    public JsonObject slackJsonGet(String accessToken) throws IOException {
        return slackJsonGet(accessToken, "conversations.list");
    }

    public JsonObject slackJsonGet(String accessToken, String apiName) throws IOException {
        return JsonParser.parseToObject(
                new HttpUrl("https://slack.com/api/" + apiName)
                .addParameter("token", accessToken)
                .toURL()
        );
    }

}
