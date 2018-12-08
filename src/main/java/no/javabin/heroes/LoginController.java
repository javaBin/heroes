package no.javabin.heroes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.PathParam;
import no.javabin.infrastructure.http.server.RequestParam;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class LoginController {

    private final ProfileContext profileContext;
    private final HeroesRepository repository;

    public LoginController(ProfileContext oauth2Configuration, DataSource dataSource) {
        this.profileContext = oauth2Configuration;
        this.repository = new HeroesRepository(dataSource);
    }

    @Get("/userinfo")
    public JsonObject getUserinfo(@SessionParameter("profile") Optional<Profile> profile) {
        if (!profile.isPresent()) {
            return new JsonObject().put("authenticated", false);
        }
        return new JsonObject()
                .put("admin", profile.get().isAdmin())
                .put("username", profile.get().getUsername())
                .put("authenticated", true);
    }

    @Get("/heroes")
    public JsonArray listHeroes(@SessionParameter("profile") Optional<Profile> profile) {
        boolean admin = profile.map(p -> p.isAdmin()).orElse(false);
        List<Hero> list = repository.list(admin);
        return JsonArray.map(list,
                hero -> new JsonObject()
                    .put("name", hero.getEmail())
                    .put("achievement", hero.getAchievement())
                    .put("published", hero.getConsentedAt() != null));
    }

    @Get("/login")
    public URL login(HttpSession session) throws MalformedURLException {
        String state = UUID.randomUUID().toString();
        session.setAttribute("loginState", state);
        return profileContext.createAuthorizationUrl(state).toURL();
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

        Profile profile = profileContext.exchangeCodeForProfile(code);

        request.getSession().invalidate();
        request.getSession(true).setAttribute("profile", profile);

        return new URL("http://localhost:9093/");
    }


}
