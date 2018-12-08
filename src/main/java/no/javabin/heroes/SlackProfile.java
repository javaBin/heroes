package no.javabin.heroes;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import no.javabin.infrastructure.http.HttpUrl;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

public class SlackProfile implements Profile {

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    @Override
    public List<JsonObject> listUsers() throws IOException {
        return slackJsonGet(accessToken, "users.list")
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

    public SlackProfile(JsonObject tokenResponse) throws IOException {
        this.accessToken = tokenResponse.requiredString("access_token");

        JsonObject userProfile = slackJsonGet(accessToken, "users.profile.get");
        this.username = userProfile.requiredObject("profile").requiredString("real_name");
        this.email = userProfile.requiredObject("profile").requiredString("email");

        JsonObject conversations = slackJsonGet(accessToken, "conversations.list");
        List<String> channelNames = conversations.requiredArray("channels")
            .objects(o -> o.requiredString("name"));
        admin = channelNames.contains("admin");
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
