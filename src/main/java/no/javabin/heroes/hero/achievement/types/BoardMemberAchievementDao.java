package no.javabin.heroes.hero.achievement.types;

import java.sql.SQLException;
import java.time.Year;
import java.util.UUID;

import no.javabin.heroes.hero.achievement.HeroAchievementDao;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextUpdateBuilder;
import org.fluentjdbc.DbInsertContext.DbInsertContextWithPk;

public class BoardMemberAchievementDao extends HeroAchievementDao<BoardMemberAchievement> {

    public BoardMemberAchievementDao(DbContext context) {
        super(context.table("achievement_board_member"));
    }

    @Override
    protected void insertFields(DbInsertContextWithPk<UUID> insertBuilder, BoardMemberAchievement achievement) {
        insertBuilder
            .setField("role", achievement.getRole().name())
            .setField("year", achievement.getYear().getValue());
    }

    @Override
    protected void updateFields(DbContextUpdateBuilder updateBuilder, BoardMemberAchievement achievement) {
        updateBuilder
            .setFieldIfPresent("role", achievement.getRole().name())
            .setFieldIfPresent("year", achievement.getYear().getValue());
    }

    @Override
    protected BoardMemberAchievement mapRow(DatabaseRow row) throws SQLException {
        BoardMemberAchievement achievement = new BoardMemberAchievement();
        achievement.setId(row.getUUID("id"));
        achievement.setRole(BoardMemberRole.valueOf(row.getString("role")));
        achievement.setYear(Year.of(row.getLong("year").intValue()));
        return achievement;
    }
}
