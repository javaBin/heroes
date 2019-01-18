package no.javabin.heroes.api;

import java.time.Instant;

import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.PathParam;
import no.javabin.infrastructure.http.server.Post;
import no.javabin.infrastructure.http.server.RequestParam;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.fluentjdbc.DbContext;
import org.jsonbuddy.JsonNull;
import org.jsonbuddy.JsonObject;

public class ProfileController {
    private HeroesRepository heroesRepository;

    public ProfileController(DbContext dbContext) {
        heroesRepository = new HeroesRepository(dbContext);
    }

    @Get("/profiles/mine")
    public JsonObject getMyProfile(@SessionParameter("profile") Profile profile) {
        JsonObject jsonProfile = new JsonObject()
                .put("name", profile.getUsername())
                .put("twitter", profile.getTwitterHandle())
                .put("email", profile.getEmail());
        Hero hero = heroesRepository.retrieveByEmail(profile.getEmail());
        if (hero == null) {
            return new JsonObject()
                    .put("profile", jsonProfile);
        }
        return new JsonObject()
                .put("profile", jsonProfile)
                .put("heroism", new JsonObject()
                        .put("achievement", hero.getAchievement()))
                .put("published", hero.getConsentedAt() != null)
                .put("consent", hero.getConsentedAt() == null
                        ? new JsonObject()
                            .put("id", 123)
                            .put("text", "Please consent to our processing of your data")
                        : new JsonNull());
    }

    @Post("/profiles/mine/consent/:consentId")
    public void consentToPublish(
            @PathParam("consentId") long consentId,
            @RequestParam.ClientIp String clientIp,
            @SessionParameter("profile") Profile profile
    ) {
        Hero hero = heroesRepository.retrieveByEmail(profile.getEmail());
        heroesRepository.updateConsent(hero.getId(), consentId, clientIp, Instant.now());
    }

}