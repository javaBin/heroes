package no.javabin.heroes.hero;

import no.javabin.heroes.hero.achievement.AchievementRepository;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbTableContext;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
            .setField("avatar_image", hero.getAvatarImage())
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
            .setFieldIfPresent("twitter", hero.getTwitter())
            .setFieldIfPresent("avatar_image", hero.getAvatarImage())
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
        List<Hero> result = table
                .whereExpression("consented_at is not null")
                .unordered()
                .list(this::mapRow);
        for (Hero hero : result) {
            retrieveAchievements(hero);
        }
        return result;
    }

    public Hero retrieveByEmail(String email) {
        Hero hero = table.where("email", email).singleObject(this::mapRow);
        if (hero != null) {
            retrieveAchievements(hero);
        }
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
        hero.setAvatarImage(o.getString("avatar_image"));
        hero.setConsentId(o.getLong("consent_id"));
        hero.setConsentClientIp(o.getString("consent_client_ip"));
        hero.setConsentedAt(o.getDateTime("consented_at"));
        return hero;
    }

}
