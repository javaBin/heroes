package no.javabin.heroes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.PathParam;
import no.javabin.infrastructure.http.server.RequestParam;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonObject;

public class ProfileController {


    private final ProfileContext oauth2Configuration;

    public ProfileController(ProfileContext oauth2Configuration) {
        this.oauth2Configuration = oauth2Configuration;
    }

    @Get("/userinfo")
    public JsonObject getUserinfo(@SessionParameter("profile") Profile profile) {
        if (profile == null) {
            return new JsonObject().put("authenticated", false);
        }
        return new JsonObject()
                .put("admin", profile.isAdmin())
                .put("username", profile.getUsername())
                .put("authenticated", true);
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

        Profile profile = oauth2Configuration.exchangeCodeForProfile(code);

        request.getSession().invalidate();
        request.getSession(true).setAttribute("profile", profile);

        return new URL("http://localhost:9093/");
    }


}
