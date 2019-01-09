package no.javabin.heroes.hero.achievement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import no.javabin.heroes.DataSourceContext;
import no.javabin.infrastructure.ExceptionUtil;
import org.jsonbuddy.JsonObject;

public class AchievementRepository {

    private DataSourceContext dataSourceContext;

    private final Map<Achievement, HeroAchievementDao<?>> daoMap = new HashMap<>();
    {
        daoMap.put(Achievement.FOREDRAGSHOLDER_JZ, new ConferenceSpeakerAchievementDao());
        daoMap.put(Achievement.FOREDRAGSHOLDER_JAVABIN, new UsergrupSpeakerAchievementDao());
        daoMap.put(Achievement.STYRE, new BoardMemberAchievementDao());
    }

    public AchievementRepository(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    public UUID add(UUID heroId, JsonObject json) {
        return add(heroId, HeroAchievement.fromJson(json));
    }

    public UUID add(UUID heroId, HeroAchievement achievement) {
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            achievement.setId(UUID.randomUUID());
            return daoMap.get(achievement.getType()).add(connection, heroId, achievement);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public void update(UUID heroId, UUID achievementId, JsonObject o) {
        HeroAchievement achievement = HeroAchievement.fromJson(o);
        achievement.setId(achievementId);
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            daoMap.get(achievement.getType()).update(connection, heroId, achievement);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public void delete(UUID heroId, UUID achievementId) {
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            for (Achievement achievement : Achievement.values()) {
                daoMap.get(achievement).delete(connection, heroId, achievementId);
            }
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public List<HeroAchievement> listAchievements(UUID heroId) {
        ArrayList<HeroAchievement> result = new ArrayList<>();
        try (Connection connection = dataSourceContext.getDataSource().getConnection()) {
            for (Achievement achievement : Achievement.values()) {
                result.addAll(daoMap.get(achievement).list(connection, heroId));
            }
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
        return result;
    }

}
