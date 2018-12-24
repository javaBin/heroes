package no.javabin.heroes.api;

import java.io.IOException;

import no.javabin.heroes.DataSourceContext;
import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.infrastructure.http.server.Body;
import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.HttpRequestException;
import no.javabin.infrastructure.http.server.Post;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class AdminController {

    private final HeroesRepository repository;

    public AdminController(DataSourceContext dataSourceContext) {
        repository = new HeroesRepository(dataSourceContext);
    }

    @Get("/admin/heroes/create")
    public JsonObject getCreateData(@SessionParameter("profile") Profile profile) throws IOException {
        if (profile == null || !profile.hasAdminScope()) {
            throw new HttpRequestException(403, "Admin login required");
        }

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

    @Post("/admin/heroes")
    public void createHero(@Body JsonObject o) {
        Hero hero = new Hero();
        hero.setEmail(o.requiredString("email"));
        hero.setAchievement(o.requiredString("achievement"));
        repository.save(hero);
    }

}