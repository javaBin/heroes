package no.javabin.heroes.hero.achievement;

import java.sql.SQLException;
import java.time.Year;
import java.util.UUID;

import org.fluentjdbc.DatabaseInsertWithPkBuilder;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTableImpl;
import org.fluentjdbc.DatabaseUpdateBuilder;

public class ConferenceSpeakerAchievementDao extends HeroAchievementDao<ConferenceSpeakerAchievement> {

    public ConferenceSpeakerAchievementDao() {
        super(new DatabaseTableImpl("achievement_conference_speaker"));
    }

    @Override
    protected void insertFields(DatabaseInsertWithPkBuilder<UUID> insertBuilder, ConferenceSpeakerAchievement achievement) {
        insertBuilder
            .setField("title", achievement.getTitle())
            .setField("year", achievement.getYear().getValue());
    }

    @Override
    protected void updateFields(DatabaseUpdateBuilder updateBuilder, ConferenceSpeakerAchievement achievement) {
        updateBuilder
            .setFieldIfPresent("title", achievement.getTitle())
            .setFieldIfPresent("year", achievement.getYear());
    }

    @Override
    protected ConferenceSpeakerAchievement mapRow(DatabaseRow row) throws SQLException {
        ConferenceSpeakerAchievement achievement = new ConferenceSpeakerAchievement();
        achievement.setId(row.getUUID("id"));
        achievement.setTitle(row.getString("title"));
        achievement.setYear(Year.of(row.getLong("year").intValue()));
        return achievement;
    }


}
