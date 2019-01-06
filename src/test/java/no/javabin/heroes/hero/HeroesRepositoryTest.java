package no.javabin.heroes.hero;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import no.javabin.heroes.TestDataSource;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import org.junit.Before;
import org.junit.Test;

public class HeroesRepositoryTest {
    private HeroesRepository heroesRepository;
    private static Random random = new Random();

    @Before
    public void setupDataSource() {
        DataSource dataSource = TestDataSource.createDataSource();
        heroesRepository = new HeroesRepository(() -> dataSource);
    }

    @Test
    public void shouldRetrieveSavedHero() {
        Hero hero = sampleHero();
        heroesRepository.save(hero);
        assertThat(hero).hasNoNullFieldsOrProperties();
        assertThat(heroesRepository.retrieveByEmail(hero.getEmail()))
            .isEqualToComparingFieldByField(hero);
        assertThat(heroesRepository.retrieveById(hero.getId()))
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

    @Test
    public void shouldUpdateHero() {
        Hero hero = basicHero();
        heroesRepository.save(hero);
        Hero update = basicHero();
        update.setId(hero.getId());
        heroesRepository.update(update);
        assertThat(heroesRepository.retrieveById(hero.getId()))
            .isEqualToComparingFieldByField(update);
    }

    public static Hero sampleHero() {
        Hero hero = basicHero();
        setConsent(hero);
        return hero;
    }

    public static void setConsent(Hero hero) {
        hero.setConsentId(random.nextLong() % 1000L);
        hero.setConsentedAt(randomInstant());
        hero.setConsentClientIp(randomIpAddress());
    }

    public static Hero basicHero() {
        Hero hero = new Hero();
        hero.setEmail(sampleEmail());
        hero.setAchievement(sampleAchievement());
        return hero;
    }

    public static Instant randomInstant() {
        return Instant.now().minusSeconds(random.nextInt(7 * 24 * 60 * 60));
    }

    public static String randomIpAddress() {
        return random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
    }

    private static String sampleAchievement() {
        String[] examples = {
                "styremedlem",
                "foredragsholder-jz",
                "foredragsholder",
                "regionsleder",
                "aktiv",
        };
        return examples[random.nextInt(examples.length)];
    }

    private static String sampleEmail() {
        return "my+email+" + UUID.randomUUID() + "@example.com";
    }


}
