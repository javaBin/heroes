package no.javabin.heroes.hero.achievement.types;

import java.sql.SQLException;
import java.util.UUID;

import no.javabin.heroes.hero.achievement.HeroAchievementDao;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextUpdateBuilder;
import org.fluentjdbc.DbInsertContext.DbInsertContextWithPk;

public class UsergrupSpeakerAchievementDao extends HeroAchievementDao<UsergroupSpeakerAchievement> {

    public UsergrupSpeakerAchievementDao(DbContext context) {
        super(context.table("achievement_usergroup_speaker"));
    }

    @Override
    protected void insertFields(DbInsertContextWithPk<UUID> insertBuilder, UsergroupSpeakerAchievement achievement) {
        insertBuilder
            .setField("title", achievement.getTitle())
            .setField("talk_date", achievement.getDate());
    }

    @Override
    protected void updateFields(DbContextUpdateBuilder updateBuilder, UsergroupSpeakerAchievement achievement) {
        updateBuilder
            .setFieldIfPresent("title", achievement.getTitle())
            .setFieldIfPresent("talk_date", achievement.getDate());
    }

    @Override
    protected UsergroupSpeakerAchievement mapRow(DatabaseRow row) throws SQLException {
        UsergroupSpeakerAchievement achievement = new UsergroupSpeakerAchievement();
        achievement.setId(row.getUUID("id"));
        achievement.setTitle(row.getString("title"));
        achievement.setDate(row.getLocalDate("talk_date"));
        return achievement;
    }

}
