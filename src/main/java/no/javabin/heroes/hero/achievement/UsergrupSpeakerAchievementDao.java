package no.javabin.heroes.hero.achievement;

import java.sql.SQLException;
import java.util.UUID;

import org.fluentjdbc.DatabaseInsertWithPkBuilder;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTableImpl;
import org.fluentjdbc.DatabaseUpdateBuilder;

public class UsergrupSpeakerAchievementDao extends HeroAchievementDao<UsergroupSpeakerAchievement> {

    public UsergrupSpeakerAchievementDao() {
        super(new DatabaseTableImpl("achievement_usergroup_speaker"));
    }

    @Override
    protected void insertFields(DatabaseInsertWithPkBuilder<UUID> insertBuilder, UsergroupSpeakerAchievement achievement) {
        insertBuilder
            .setField("title", achievement.getTitle())
            .setField("talk_date", achievement.getDate());
    }

    @Override
    protected void updateFields(DatabaseUpdateBuilder updateBuilder, UsergroupSpeakerAchievement achievement) {
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
