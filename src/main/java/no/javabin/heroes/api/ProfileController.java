package no.javabin.heroes.api;

import java.time.Instant;

import no.javabin.heroes.DataSourceContext;
import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.PathParam;
import no.javabin.infrastructure.http.server.Post;
import no.javabin.infrastructure.http.server.RequestParam;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonObject;

public class ProfileController {
    private HeroesRepository heroesRepository;

    public ProfileController(DataSourceContext dataSourceContext) {
        heroesRepository = new HeroesRepository(dataSourceContext);
    }

    @Get("/profiles/mine")
    public JsonObject getMyProfile(@SessionParameter("profile") Profile profile) {
        Hero hero = heroesRepository.retrieveByEmail(profile.getEmail());
        if (hero == null) {
            return null;
        }
        return new JsonObject()
                .put("email", hero.getEmail())
                .put("published", hero.getConsentedAt() != null)
                .put("consent", new JsonObject()
                        .put("id", 123)
                        .put("text", "Please consent to our processing of your data"));
    }

    @Post("/profiles/mine/consent/:consentId")
    public void consentToPublish(
            @PathParam("consentId") String consentId,
            @RequestParam.ClientIp String clientIp,
            @SessionParameter("profile") Profile profile
    ) {
        Hero hero = heroesRepository.retrieveByEmail(profile.getEmail());
        hero.setConsentId(Long.parseLong(consentId));
        hero.setConsentedAt(Instant.now());
        hero.setConsentClientIp(clientIp);
        heroesRepository.update(hero);
    }

}