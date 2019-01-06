package no.javabin.heroes.hero.achievement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import no.javabin.heroes.DataSourceContext;
import no.javabin.infrastructure.ExceptionUtil;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTable;
import org.fluentjdbc.DatabaseTableImpl;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;

public class AchievementRepository {

    private final DatabaseTable table = new DatabaseTableImpl("hero_achievements");
    private DataSourceContext dataSourceContext;

    public AchievementRepository(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    public void add(UUID heroId, JsonObject heroAchievement) {
        String type = heroAchievement.requiredString("type");
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            table.insert()
                .setPrimaryKey("id", UUID.randomUUID())
                .setField("achievement_type", type)
                .setField("hero_id", heroId)
                .setField("data", heroAchievement.toJson())
                .execute(connection);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public void update(UUID heroId, UUID achievementId, JsonObject o) {
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            JsonObject data = JsonParser.parseToObject(table.where("id", achievementId).singleString(connection, "data"));
            data.putAll(o);
            table.where("id", achievementId).update().setField("data", data.toJson()).execute(connection);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public void delete(UUID heroId, UUID achievementId) {
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            table.where("id", achievementId).delete(connection);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public List<HeroAchievement> listByHeroId(UUID heroId) {
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            return table
                    .where("hero_id", heroId)
                    .unordered()
                    .list(connection, this::mapRowToAchievement);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    private HeroAchievement mapRowToAchievement(DatabaseRow row) throws SQLException {
        HeroAchievement achievement = new HeroAchievement();
        achievement.setId(row.getUUID("id"));
        achievement.setType(row.getString("achievement_type"));
        achievement.setData(row.getString("data"));
        return achievement;
    }

}
