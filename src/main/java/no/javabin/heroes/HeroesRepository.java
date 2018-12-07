package no.javabin.heroes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import no.javabin.infrastructure.ExceptionUtil;
import org.fluentjdbc.DatabaseRow;
import org.fluentjdbc.DatabaseTable;
import org.fluentjdbc.DatabaseTableImpl;

public class HeroesRepository {

    private final DataSource dataSource;
    private final DatabaseTable table = new DatabaseTableImpl("heroes");

    public HeroesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Hero hero) {
        try (Connection conn = dataSource.getConnection()) {
            table.insert()
                .setPrimaryKey("id", UUID.randomUUID())
                .setField("email", hero.getEmail())
                .setField("achievement", hero.getAchievement())
                .setField("consent_id", hero.getConsentId())
                .setField("consented_at", hero.getConsentedAt())
                .setField("consent_client_ip", hero.getConsentClientIp())
                .execute(conn);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public void update(Hero hero) {
        try (Connection conn = dataSource.getConnection()) {
            table
                .where("email", hero.getEmail())
                .update()
                .setField("achievement", hero.getAchievement())
                .setField("consent_id", hero.getConsentId())
                .setField("consented_at", hero.getConsentedAt())
                .setField("consent_client_ip", hero.getConsentClientIp())
                .execute(conn);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }


    public List<Hero> list() {
        try (Connection conn = dataSource.getConnection()) {
            return table
                    .whereExpression("consented_at is not null")
                    .unordered()
                    .list(conn, this::mapRow);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    public Hero retrieveByEmail(String email) {
        try (Connection conn = dataSource.getConnection()) {
            return table.where("email", email).singleObject(conn, this::mapRow);
        } catch (SQLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    private Hero mapRow(DatabaseRow o) throws SQLException {
        Hero hero = new Hero();
        hero.setEmail(o.getString("email"));
        hero.setAchievement(o.getString("achievement"));
        hero.setConsentId(o.getLong("consent_id"));
        hero.setConsentClientIp(o.getString("consent_client_ip"));
        hero.setConsentedAt(o.getDateTime("consented_at"));
        return hero;
    }

}
