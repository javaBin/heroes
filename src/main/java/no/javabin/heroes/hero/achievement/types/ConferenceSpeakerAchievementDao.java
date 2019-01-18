package no.javabin.heroes.hero.achievement.types;

import java.sql.SQLException;
import java.time.Year;
import java.util.UUID;

import no.javabin.heroes.hero.achievement.HeroAchievementDao;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextUpdateBuilder;
import org.fluentjdbc.DbInsertContext.DbInsertContextWithPk;

public class ConferenceSpeakerAchievementDao extends HeroAchievementDao<ConferenceSpeakerAchievement> {

    public ConferenceSpeakerAchievementDao(DbContext context) {
        super(context.table("achievement_conference_speaker"));
    }

    @Override
    protected void insertFields(DbInsertContextWithPk<UUID> insertBuilder, ConferenceSpeakerAchievement achievement) {
        insertBuilder
            .setField("title", achievement.getTitle())
            .setField("year", achievement.getYear().getValue());
    }

    @Override
    protected void updateFields(DbContextUpdateBuilder updateBuilder, ConferenceSpeakerAchievement achievement) {
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
