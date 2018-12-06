package no.javabin.heroes;

import java.io.IOException;

import no.javabin.infrastructure.http.server.Body;
import no.javabin.infrastructure.http.server.Get;
import no.javabin.infrastructure.http.server.Post;
import no.javabin.infrastructure.http.server.SessionParameter;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonObject;

public class AdminController {

    @Get("/admin/heroes/create")
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

    @Post("/admin/heroes")
    public void createHero(@Body JsonObject hero) {
    }

}