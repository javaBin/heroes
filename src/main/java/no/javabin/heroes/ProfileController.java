package no.javabin.heroes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jsonbuddy.JsonObject;

public class ProfileController {

    private final Oauth2Configuration oauth2Configuration;

    public ProfileController(Oauth2Configuration oauth2Configuration) {
        this.oauth2Configuration = oauth2Configuration;
    }

    @Get("/userinfo")
    public JsonObject getUserinfo(@SessionParameter("profile") JsonObject profile) {
        return new JsonObject()
                .put("profile", profile)
                .put("admin", true)
                .put("username", profile != null ? profile.requiredString("name") : null)
                .put("authenticated", profile != null);
    }

    @Get("/login")
    public URL login(HttpSession session) throws MalformedURLException {
        String state = UUID.randomUUID().toString();
        session.setAttribute("loginState", state);
        return oauth2Configuration.createAuthorizationUrl(state).toURL();
    }


    @Get("/oauth2callback/:provider")
    public URL oauth2Callback(
            @PathParam("provider") String provider,
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @SessionParameter("loginState") String loginState,
            HttpServletRequest request
    ) throws IOException {
        if (!state.equals(loginState)) {
            request.getSession().invalidate();
            throw new HttpRequestException(400, "Invalidate state");
        }

        JsonObject token = oauth2Configuration.exchangeCodeForToken(code);

        request.getSession().invalidate();
        request.getSession(true).setAttribute("profile", token.requiredObject("user"));

        return new URL("http://localhost:9093/");
    }


}
