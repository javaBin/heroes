package no.javabin.heroes.api;

import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.heroes.hero.achievement.AchievementRepository;
import org.actioncontroller.*;
import org.actioncontroller.json.JsonBody;
import org.fluentjdbc.DbContext;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AdminController {

    private final HeroesRepository repository;
    private final AchievementRepository achievementRepository;

    AdminController(DbContext dbContext) {
        repository = new HeroesRepository(dbContext);
        achievementRepository = new AchievementRepository(dbContext);
    }

    @Get("/admin/heroes")
    @RequireUserRole("admin")
    @JsonBody
    public List<Hero> getAllObjects() {
        return repository.list(true);
    }

    @Get("/admin/heroes/create")
    @RequireUserRole("admin")
    @JsonBody
    public JsonObject getCreateData(@SessionParameter("profile") Profile profile) throws IOException {
        return new JsonObject()
                .put("people", JsonArray.fromNodeList(profile.listUsers()));
    }

    @Get("/heroes/:heroId")
    @RequireUserRole("admin")
    @JsonBody
    public Hero getHeroDetails(@PathParam("heroId") UUID heroId) {
        return repository.retrieveById(heroId);
    }

    @Post("/admin/heroes")
    @RequireUserRole("admin")
    public void createHero(@JsonBody Hero hero) {
        repository.save(hero);
    }

    @Put("/admin/heroes/:heroId")
    @RequireUserRole("admin")
    public void updateHero(@PathParam("heroId") UUID heroId, @JsonBody Hero update) {
        Hero hero = new Hero();
        hero.setId(heroId);
        hero.setEmail(update.getEmail());
        hero.setName(update.getName());
        hero.setTwitter(update.getTwitter());
        hero.setAvatarImage(update.getAvatarImage());
        repository.update(hero);
    }

    @Post("/admin/heroes/:heroId/achievements")
    @RequireUserRole("admin")
    public void addAchievement(
            @PathParam("heroId") UUID heroId,
            @JsonBody JsonObject o
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
            @JsonBody JsonObject o
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
