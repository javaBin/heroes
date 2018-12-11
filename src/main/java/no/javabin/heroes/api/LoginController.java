package no.javabin.heroes.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import no.javabin.heroes.Profile;
import no.javabin.heroes.ProfileContext;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesContext;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.HttpRequestException;
import no.javabin.infrastructure.http.server.RequestParam;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class LoginController {

    private final ProfileContext profileContext;
    private final HeroesRepository repository;

    public LoginController(HeroesContext heroesContext) {
        this.profileContext = heroesContext;
        this.repository = new HeroesRepository(heroesContext);
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
    public URL login(
            @RequestParam("admin") Optional<Boolean> admin,
            @SessionParameter("loginState") Consumer<String> setLoginState
    ) throws MalformedURLException {
        boolean needsAdmin = admin.orElse(false);
        String state = UUID.randomUUID().toString();
        setLoginState.accept(state);
        return profileContext.createAuthorizationUrl(state, needsAdmin).toURL();
    }


    @Get("/oauth2callback/:provider")
    //@SendRedirect
    public String oauth2Callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            @SessionParameter("loginState") String loginState,
            @SessionParameter(value = "profile", invalidate = true) Consumer<Profile> setSessionProfile
    ) throws IOException {
        if (!state.equals(loginState)) {
            throw new HttpRequestException(400, "Invalidate state");
        }

        Profile profile = profileContext.exchangeCodeForProfile(code);
        setSessionProfile.accept(profile);
        return "/";
    }


}
