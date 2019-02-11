package no.javabin.heroes.api;

import no.javabin.heroes.Profile;
import no.javabin.heroes.ProfileContext;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesContext;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.infrastructure.http.server.*;
import no.javabin.infrastructure.http.server.json.JsonBody;
import org.fluentjdbc.DbContext;
import org.jsonbuddy.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class PublicController {

    private final ProfileContext profileContext;
    private final HeroesRepository repository;

    public PublicController(HeroesContext heroesContext, DbContext dbContext) {
        this.profileContext = heroesContext;
        this.repository = new HeroesRepository(dbContext);
    }

    @Get("/userinfo")
    @JsonBody
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
    @JsonBody
    public List<Hero> listHeroes() {
        return repository.list(false);
    }

    @Get("/login")
    public URL login(
            @RequestParam("admin") Optional<Boolean> admin,
            @SessionParameter("loginState") Consumer<String> setLoginState
    ) {
        boolean needsAdmin = admin.orElse(false);
        String state = UUID.randomUUID().toString();
        setLoginState.accept(state);
        return profileContext.createAuthorizationUrl(state, needsAdmin).toURL();
    }


    @Get("/oauth2callback/:provider")
    @SendRedirect
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
