package no.javabin.heroes.hero;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import no.javabin.heroes.hero.achievement.AchievementRepository;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbTableContext;

public class HeroesRepository {

    private AchievementRepository achievementRepository;
    private DbTableContext table;

    public HeroesRepository(DbContext dbContext) {
        achievementRepository = new AchievementRepository(dbContext);
        table = dbContext.table("heroes");
    }

    public void save(Hero hero) {
        UUID id = table.newSaveBuilderWithUUID("id", null)
            .uniqueKey("email", hero.getEmail())
            .setField("name", hero.getName())
            .setField("twitter", hero.getTwitter())
            .setField("achievement", hero.getAchievement())
            .setField("consent_id", hero.getConsentId())
            .setField("consented_at", hero.getConsentedAt())
            .setField("consent_client_ip", hero.getConsentClientIp())
            .execute();
        hero.setId(id);
    }

    public void update(Hero hero) {
        table
            .where("id", hero.getId())
            .update()
            .setField("email", hero.getEmail())
            .setField("name", hero.getName())
            .setField("twitter", hero.getTwitter())
            .setField("achievement", hero.getAchievement())
            .execute();
    }

    public void updateConsent(UUID id, long consentId, String clientIp, Instant consentedTime) {
        table
            .where("id", id)
            .update()
            .setField("consent_id", consentId)
            .setField("consented_at", consentedTime)
            .setField("consent_client_ip", clientIp)
            .execute();
    }

    public List<Hero> list(boolean includeUnpublished) {
        if (includeUnpublished) {
            return table.listObjects(this::mapRow);
        }
        return table
                .whereExpression("consented_at is not null")
                .unordered()
                .list(this::mapRow);
    }

    public Hero retrieveByEmail(String email) {
        Hero hero = table.where("email", email).singleObject(this::mapRow);
        retrieveAchievements(hero);
        return hero;
    }

    public Hero retrieveById(UUID id) {
        Hero hero = table.where("id", id).singleObject(this::mapRow);
        retrieveAchievements(hero);
        return hero;
    }

    private void retrieveAchievements(Hero hero) {
        hero.setAchievements(achievementRepository.listAchievements(hero.getId()));
    }

    private Hero mapRow(DatabaseRow o) throws SQLException {
        Hero hero = new Hero();
        hero.setId(o.getUUID("id"));
        hero.setEmail(o.getString("email"));
        hero.setName(o.getString("name"));
        hero.setTwitter(o.getString("twitter"));
        hero.setAchievement(o.getString("achievement"));
        hero.setConsentId(o.getLong("consent_id"));
        hero.setConsentClientIp(o.getString("consent_client_ip"));
        hero.setConsentedAt(o.getDateTime("consented_at"));
        return hero;
    }

}
