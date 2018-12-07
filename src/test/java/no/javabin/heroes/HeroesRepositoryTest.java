package no.javabin.heroes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

public class HeroesRepositoryTest {
    private DataSource dataSource;
    private HeroesRepository heroesRepository;

    @Before
    public void setupDataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource = jdbcDataSource;
        Flyway.configure().dataSource(dataSource).load().migrate();

        heroesRepository = new HeroesRepository(dataSource);
    }

    @Test
    public void shouldListSavedHeroes() {
        Hero hero = sampleHero();

        heroesRepository.save(hero);
        assertThat(heroesRepository.list())
            .contains(hero);
    }

    private Hero sampleHero() {
        Hero hero = new Hero();
        hero.setEmail(sampleEmail());
        hero.setAchievement(sampleAchievement());
        return hero;
    }

    private String sampleAchievement() {
        String[] examples = {
                "styremedlem",
                "foredragsholder-jz",
                "foredragsholder",
                "regionsleder",
                "aktiv",
        };
        return examples[new Random().nextInt(examples.length)];
    }

    private String sampleEmail() {
        return "my+email+" + UUID.randomUUID() + "@example.com";
    }


}
