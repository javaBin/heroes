package no.javabin.heroes.api;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.heroes.hero.achievement.AchievementRepository;
import no.javabin.infrastructure.http.Delete;
import no.javabin.infrastructure.http.server.Body;
import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.HttpRequestException;
import no.javabin.infrastructure.http.server.PathParam;
import no.javabin.infrastructure.http.server.Post;
import no.javabin.infrastructure.http.server.Put;
import no.javabin.infrastructure.http.server.RequireUserRole;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.fluentjdbc.DbContext;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class AdminController {

    private final HeroesRepository repository;
    private final AchievementRepository achievementRepository;

    public AdminController(DbContext dbContext) {
        repository = new HeroesRepository(dbContext);
        achievementRepository = new AchievementRepository(dbContext);
    }

    @Get("/admin/heroes")
    @RequireUserRole("admin")
    public JsonArray getAllObjects() {
        List<Hero> list = repository.list(true);
        return JsonArray.map(list,
                hero -> new JsonObject()
                    .put("name", hero.getName())
                    .put("email", hero.getEmail())
                    .put("id", hero.getId().toString())
                    .put("achievements", new JsonArray())
                    .put("published", hero.getConsentedAt() != null));

    }

    @Get("/admin/heroes/create")
    @RequireUserRole("admin")
    public JsonObject getCreateData(@SessionParameter("profile") Profile profile) throws IOException {
        return new JsonObject()
                .put("people", JsonArray.fromNodeList(profile.listUsers()));
    }

    @Get("/heroes/:heroId")
    @RequireUserRole("admin")
    public JsonObject getHeroDetails(@PathParam("heroId") UUID heroId) {
        Hero hero = repository.retrieveById(heroId);
        return new JsonObject()
                .put("email", hero.getEmail())
                .put("name", hero.getName())
                .put("twitter", hero.getTwitter())
                .put("id", hero.getId().toString())
                .put("achievements", JsonArray.map(hero.getAchievements(),
                        a -> a.toJSON()))
                .put("published", hero.getConsentedAt() != null);
    }

    @Post("/admin/heroes")
    @RequireUserRole("admin")
    public void createHero(@Body JsonObject o) {
        Hero hero = new Hero();
        hero.setEmail(o.requiredString("email"));
        hero.setName(o.requiredString("name"));
        hero.setTwitter(o.stringValue("twitter").orElse(null));
        hero.setAchievement(o.stringValue("achievement").orElse(null));
        repository.save(hero);
    }

    @Put("/admin/heroes/:heroId")
    @RequireUserRole("admin")
    public void updateHero(@PathParam("heroId") UUID heroId, @Body JsonObject o) {
        Hero hero = new Hero();
        hero.setId(heroId);
        hero.setEmail(o.requiredString("email"));
        hero.setName(o.requiredString("name"));
        hero.setTwitter(o.stringValue("twitter").orElse(null));
        hero.setAchievement(o.stringValue("achievement").orElse(null));
        repository.update(hero);
    }

    @Post("/admin/heroes/:heroId/achievements")
    @RequireUserRole("admin")
    public void addAchievement(
            @PathParam("heroId") UUID heroId,
            @Body JsonObject o
    ) {
        if (!o.containsKey("type")) {
            throw new HttpRequestException(400, "Missing JSON field `type`");
        }
        achievementRepository.add(heroId, o);
    }

    @Put("/admin/heroes/:heroId/achievements/:achievementId")
    @RequireUserRole("admin")
    public void updateAchievement(
            @PathParam("heroId") UUID heroId,
            @PathParam("achievementId") UUID achievementId,
            @Body JsonObject o
    ) {
        achievementRepository.update(heroId, achievementId, o);
    }

    @Delete("/admin/heroes/:heroId/achievements/:achievementId")
    @RequireUserRole("admin")
    public void updateAchievement(
            @PathParam("heroId") UUID heroId,
            @PathParam("achievementId") UUID achievementId
    ) {
        achievementRepository.delete(heroId, achievementId);
    }

}
