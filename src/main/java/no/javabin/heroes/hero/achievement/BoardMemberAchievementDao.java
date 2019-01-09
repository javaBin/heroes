package no.javabin.heroes.hero.achievement;

import java.sql.SQLException;
import java.time.Year;
import java.util.UUID;

import org.fluentjdbc.DatabaseInsertWithPkBuilder;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTableImpl;
import org.fluentjdbc.DatabaseUpdateBuilder;

public class BoardMemberAchievementDao extends HeroAchievementDao<BoardMemberAchievement> {

    public BoardMemberAchievementDao() {
        super(new DatabaseTableImpl("achievement_board_member"));
    }

    @Override
    protected void insertFields(DatabaseInsertWithPkBuilder<UUID> insertBuilder, BoardMemberAchievement achievement) {
        insertBuilder
            .setField("role", achievement.getRole().name())
            .setField("year", achievement.getYear().getValue());
    }

    @Override
    protected void updateFields(DatabaseUpdateBuilder updateBuilder, BoardMemberAchievement achievement) {
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
