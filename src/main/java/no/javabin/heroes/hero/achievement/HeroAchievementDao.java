package no.javabin.heroes.hero.achievement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.fluentjdbc.DatabaseInsertWithPkBuilder;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTable;
import org.fluentjdbc.DatabaseUpdateBuilder;

public abstract class HeroAchievementDao<T extends HeroAchievement> {

    protected final DatabaseTable table;

    public HeroAchievementDao(DatabaseTable table) {
        this.table = table;
    }

    @SuppressWarnings("unchecked")
    public UUID add(Connection connection, UUID heroId, HeroAchievement achievement) throws SQLException {
        DatabaseInsertWithPkBuilder<UUID> insertBuilder = table.insert()
            .setPrimaryKey("id", achievement.getId())
            .setField("hero_id", heroId);
        insertFields(insertBuilder, (T)achievement);
        insertBuilder.execute(connection);
        return achievement.getId();
    }

    protected abstract void insertFields(DatabaseInsertWithPkBuilder<UUID> insertBuilder, T achievement);

    public List<? extends HeroAchievement> list(Connection connection, UUID heroId) {
        return table.where("hero_id", heroId).unordered()
                .list(connection, this::mapRow);
    }

    protected abstract T mapRow(DatabaseRow row) throws SQLException;

    public void delete(Connection connection, UUID heroId, UUID achievementId) {
        table.where("id", achievementId).where("hero_id",  heroId)
            .delete(connection);
    }

    @SuppressWarnings("unchecked")
    public void update(Connection connection, UUID heroId, HeroAchievement achievement) {
        DatabaseUpdateBuilder updateBuilder = table.where("id", achievement.getId()).where("hero_id",  heroId)
            .update();
        updateFields(updateBuilder, (T) achievement);
        updateBuilder.execute(connection);
    }

    protected abstract void updateFields(DatabaseUpdateBuilder updateBuilder, T achievement);

}
