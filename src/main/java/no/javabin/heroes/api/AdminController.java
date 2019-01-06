package no.javabin.heroes.api;

import java.io.IOException;
import java.util.UUID;

import no.javabin.heroes.DataSourceContext;
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
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class AdminController {

    private final HeroesRepository repository;
    private final AchievementRepository achievementRepository;

    public AdminController(DataSourceContext dataSourceContext) {
        repository = new HeroesRepository(dataSourceContext);
        achievementRepository = new AchievementRepository(dataSourceContext);
    }

    @Get("/admin/heroes/create")
    @RequireUserRole("admin")
    public JsonObject getCreateData(@SessionParameter("profile") Profile profile) throws IOException {
        JsonArray achievementTypes = new JsonArray()
                .add(new JsonObject().put("value", "styremedlem").put("label", "Styremedlem"))
                .add(new JsonObject().put("value", "foredragsholder-jz").put("label", "Foredragsholder på JavaZone"))
                .add(new JsonObject().put("value", "foredragsholder").put("label", " Foredragsholder på javaBin"))
                .add(new JsonObject().put("value", "regionsleder").put("label", "Regionsleder"))
                .add(new JsonObject().put("value", "aktiv").put("label", "Aktiv"))
                ;
        return new JsonObject()
                .put("people", JsonArray.fromNodeList(profile.listUsers()))
                .put("achievements", achievementTypes);
    }

    @Get("/heroes/:heroId")
    @RequireUserRole("admin")
    public JsonObject getHeroDetails(@PathParam("heroId") UUID heroId) {
        Hero hero = repository.retrieveById(heroId);
        return new JsonObject()
                .put("name", hero.getEmail())
                .put("id", hero.getId().toString())
                .put("achievements", JsonArray.map(hero.getAchievements(),
                        a -> new JsonObject().put("id", a.getId().toString()).put("label", a.getLabel()).put("type", a.getType())))
                .put("published", hero.getConsentedAt() != null);
    }

    @Post("/admin/heroes")
    @RequireUserRole("admin")
    public void createHero(@Body JsonObject o) {
        Hero hero = new Hero();
        hero.setEmail(o.requiredString("email"));
        hero.setAchievement(o.stringValue("achievement").orElse(null));
        repository.save(hero);
    }

    @Put("/admin/heroes/:heroId")
    @RequireUserRole("admin")
    public void updateHero(@PathParam("heroId") UUID heroId, @Body JsonObject o) {
        Hero hero = new Hero();
        hero.setId(heroId);
        hero.setEmail(o.requiredString("email"));
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
