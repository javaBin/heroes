package no.javabin.heroes;

import java.time.Instant;

import javax.sql.DataSource;

import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.PathParam;
import no.javabin.infrastructure.http.server.Post;
import no.javabin.infrastructure.http.server.RequestParam;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonObject;

public class ProfileController {
    private HeroesRepository heroesRepository;

    public ProfileController(DataSource dataSource) {
        heroesRepository = new HeroesRepository(dataSource);
    }

    @Get("/profiles/mine")
    public JsonObject getMyProfile(@SessionParameter("profile") Profile profile) {
        Hero hero = heroesRepository.retrieveByEmail(profile.getUsername());
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
            @PathParam("consentId") long consentId,
            @RequestParam.ClientIp String clientIp,
            @SessionParameter("profile") Profile profile
    ) {
        Hero hero = heroesRepository.retrieveByEmail(profile.getUsername());
        hero.setConsentId(consentId);
        hero.setConsentedAt(Instant.now());
        hero.setConsentClientIp(clientIp);
        heroesRepository.update(hero);
    }

}