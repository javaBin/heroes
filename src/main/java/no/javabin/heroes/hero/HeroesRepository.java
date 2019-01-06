package no.javabin.heroes.hero;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import no.javabin.heroes.DataSourceContext;
import no.javabin.heroes.hero.achievement.AchievementRepository;
import no.javabin.infrastructure.ExceptionUtil;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTable;
import org.fluentjdbc.DatabaseTableImpl;

public class HeroesRepository {

    private final DatabaseTable table = new DatabaseTableImpl("heroes");
    private DataSourceContext dataSourceContext;
    private AchievementRepository achievementRepository;

    public HeroesRepository(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
        achievementRepository = new AchievementRepository(dataSourceContext);
    }

    public void save(Hero hero) {
        try (Connection conn = getConnection()) {
            UUID id = table.newSaveBuilderWithUUID("id", null)
                .uniqueKey("email", hero.getEmail())
                .setField("achievement", hero.getAchievement())
                .setField("consent_id", hero.getConsentId())
                .setField("consented_at", hero.getConsentedAt())
                .setField("consent_client_ip", hero.getConsentClientIp())
                .execute(conn);
            hero.setId(id);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public void update(Hero hero) {
        try (Connection conn = getConnection()) {
            table
                .where("id", hero.getId())
                .update()
                .setField("email", hero.getEmail())
                .setField("achievement", hero.getAchievement())
                .setField("consent_id", hero.getConsentId())
                .setField("consented_at", hero.getConsentedAt())
                .setField("consent_client_ip", hero.getConsentClientIp())
                .execute(conn);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public List<Hero> list(boolean includeUnpublished) {
        try (Connection conn = getConnection()) {
            if (includeUnpublished) {
                return table.listObjects(conn, this::mapRow);
            }
            return table
                    .whereExpression("consented_at is not null")
                    .unordered()
                    .list(conn, this::mapRow);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public Hero retrieveByEmail(String email) {
        try (Connection conn = getConnection()) {
            Hero hero = table.where("email", email).singleObject(conn, this::mapRow);
            retrieveAchievements(hero);
            return hero;
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public Hero retrieveById(UUID id) {
        try (Connection conn = getConnection()) {
            Hero hero = table.where("id", id).singleObject(conn, this::mapRow);
            retrieveAchievements(hero);
            return hero;
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    private void retrieveAchievements(Hero hero) {
        hero.setAchievements(achievementRepository.listByHeroId(hero.getId()));
    }

    private Hero mapRow(DatabaseRow o) throws SQLException {
        Hero hero = new Hero();
        hero.setId(o.getUUID("id"));
        hero.setEmail(o.getString("email"));
        hero.setAchievement(o.getString("achievement"));
        hero.setConsentId(o.getLong("consent_id"));
        hero.setConsentClientIp(o.getString("consent_client_ip"));
        hero.setConsentedAt(o.getDateTime("consented_at"));
        return hero;
    }

    private Connection getConnection() throws SQLException {
        return dataSourceContext.getDataSource().getConnection();
    }


}
