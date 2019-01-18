package no.javabin.heroes.hero.achievement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.fluentjdbc.DbContext;
import org.jsonbuddy.JsonObject;

public class AchievementRepository {

    private DbContext context;

    public AchievementRepository(DbContext context) {
        this.context = context;
    }

    public UUID add(UUID heroId, JsonObject json) {
        return add(heroId, HeroAchievement.fromJson(json));
    }

    public UUID add(UUID heroId, HeroAchievement achievement) {
        achievement.setId(UUID.randomUUID());
        return achievement.getType().getDao(context).add(heroId, achievement);
    }

    public void update(UUID heroId, UUID achievementId, JsonObject o) {
        HeroAchievement achievement = HeroAchievement.fromJson(o);
        achievement.setId(achievementId);
        achievement.getType().getDao(context).update(heroId, achievement);
    }

    public void delete(UUID heroId, UUID achievementId) {
        for (Achievement achievement : Achievement.values()) {
            achievement.getDao(context).delete(heroId, achievementId);
        }
    }

    public List<HeroAchievement> listAchievements(UUID heroId) {
        List<HeroAchievement> result = new ArrayList<>();
        for (Achievement achievement : Achievement.values()) {
            result.addAll(achievement.getDao(context).list(heroId));
        }
        return result;
    }

}
