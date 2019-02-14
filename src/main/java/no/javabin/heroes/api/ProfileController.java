package no.javabin.heroes.api;

import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import org.actioncontroller.*;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonNull;
import org.jsonbuddy.JsonObject;

import java.time.Instant;

public class ProfileController {
    private HeroesRepository heroesRepository;

    ProfileController(DbContext dbContext) {
        heroesRepository = new HeroesRepository(dbContext);
    }

    @Get("/profiles/mine")
    @JsonBody
    public JsonObject getMyProfile(@SessionParameter("profile") Profile profile) {
        JsonObject jsonProfile = new JsonObject()
                .put("name", profile.getUsername())
                .put("twitter", profile.getTwitterHandle())
                .put("email", profile.getEmail())
                ;
        Hero hero = heroesRepository.retrieveByEmail(profile.getEmail());
        if (hero == null) {
            return new JsonObject()
                    .put("profile", jsonProfile);
        }
        return new JsonObject()
                .put("profile", jsonProfile)
                .put("published", hero.isPublished())
                .put("achievements", JsonArray.map(hero.getAchievements(),
                        a -> new JsonObject().put("label", a.getLabel())))
                .put("consent", !hero.isPublished()
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