package no.javabin.heroes.hero.achievement;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DbContextUpdateBuilder;
import org.fluentjdbc.DbInsertContext.DbInsertContextWithPk;
import org.fluentjdbc.DbTableContext;

public abstract class HeroAchievementDao<T extends HeroAchievement> {

    protected final DbTableContext table;

    public HeroAchievementDao(DbTableContext dbTableContext) {
        this.table = dbTableContext;
    }

    @SuppressWarnings("unchecked")
    public UUID add(UUID heroId, HeroAchievement achievement) {
        DbInsertContextWithPk<UUID> insertBuilder = table.insert()
            .setPrimaryKey("id", achievement.getId())
            .setField("hero_id", heroId);
        insertFields(insertBuilder, (T)achievement);
        insertBuilder.execute();
        return achievement.getId();
    }

    protected abstract void insertFields(DbInsertContextWithPk<UUID> insertBuilder, T achievement);

    public List<? extends HeroAchievement> list(UUID heroId) {
        return table.where("hero_id", heroId).unordered()
                .list(this::mapRow);
    }

    protected abstract T mapRow(DatabaseRow row) throws SQLException;

    public void delete(UUID heroId, UUID achievementId) {
        table.where("id", achievementId).where("hero_id",  heroId)
            .executeDelete();
    }

    @SuppressWarnings("unchecked")
    public void update(UUID heroId, HeroAchievement achievement) {
        DbContextUpdateBuilder updateBuilder = table.where("id", achievement.getId()).where("hero_id",  heroId)
            .update();
        updateFields(updateBuilder, (T) achievement);
        updateBuilder.execute();
    }

    protected abstract void updateFields(DbContextUpdateBuilder updateBuilder, T achievement);

}
