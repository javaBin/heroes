package no.javabin.heroes.hero;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

public class HeroesRepositoryTest {
    private DataSource dataSource;
    private HeroesRepository heroesRepository;
    private Random random = new Random();

    @Before
    public void setupDataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource = jdbcDataSource;
        Flyway.configure().dataSource(dataSource).load().migrate();

        heroesRepository = new HeroesRepository(() -> dataSource);
    }

    @Test
    public void shouldRetrieveSavedHero() {
        Hero hero = sampleHero();
        assertThat(hero).hasNoNullFieldsOrProperties();
        heroesRepository.save(hero);
        assertThat(heroesRepository.retrieveByEmail(hero.getEmail()))
            .isEqualToComparingFieldByField(hero);
    }

    @Test
    public void shouldListSavedHeroes() {
        Hero hero = sampleHero();
        heroesRepository.save(hero);
        assertThat(heroesRepository.list(true))
            .contains(hero);
    }

    @Test
    public void shouldHideHeroesWithoutConsent() {
        Hero hero = basicHero();
        heroesRepository.save(hero);
        assertThat(heroesRepository.list(false))
            .doesNotContain(hero);
        assertThat(heroesRepository.list(true))
            .contains(hero);
    }

    @Test
    public void shouldShowHeroesAfterConsent() {
        Hero hero = basicHero();
        heroesRepository.save(hero);
        setConsent(hero);
        heroesRepository.update(hero);
        assertThat(heroesRepository.list(false))
            .contains(hero);
        assertThat(heroesRepository.retrieveByEmail(hero.getEmail()))
            .isEqualToComparingFieldByField(hero);
    }

    private Hero sampleHero() {
        Hero hero = basicHero();
        setConsent(hero);
        return hero;
    }

    public void setConsent(Hero hero) {
        hero.setConsentId(random.nextLong() % 1000L);
        hero.setConsentedAt(randomInstant());
        hero.setConsentClientIp(randomIpAddress());
    }

    public Hero basicHero() {
        Hero hero = new Hero();
        hero.setEmail(sampleEmail());
        hero.setAchievement(sampleAchievement());
        return hero;
    }

    public Instant randomInstant() {
        return Instant.now().minusSeconds(random.nextInt(7 * 24 * 60 * 60));
    }

    public String randomIpAddress() {
        return random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
    }

    private String sampleAchievement() {
        String[] examples = {
                "styremedlem",
                "foredragsholder-jz",
                "foredragsholder",
                "regionsleder",
                "aktiv",
        };
        return examples[random.nextInt(examples.length)];
    }

    private String sampleEmail() {
        return "my+email+" + UUID.randomUUID() + "@example.com";
    }


}
