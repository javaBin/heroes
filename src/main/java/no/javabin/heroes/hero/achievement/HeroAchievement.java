package no.javabin.heroes.hero.achievement;

import java.util.UUID;

import org.jsonbuddy.JsonObject;

public abstract class HeroAchievement {

    public abstract String getLabel();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Achievement getType() {
        return type;
    }

    public void setType(Achievement type) {
        this.type = type;
    }

    protected UUID id;
    private Achievement type;

    public static HeroAchievement fromJson(JsonObject json) {
        Achievement type = json.requiredEnum("type", Achievement.class);
        return type.newInstance().readFields(json);
    }

    protected abstract HeroAchievement readFields(JsonObject json);

    public JsonObject toJSON() {
        JsonObject json = new JsonObject()
                .put("id", getId().toString())
                .put("label", getLabel())
                .put("type", getType());
        setFields(json);
        return json;
    }

    protected abstract void setFields(JsonObject json);

}
