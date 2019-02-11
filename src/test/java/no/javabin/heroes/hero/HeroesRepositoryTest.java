package no.javabin.heroes.hero;

import no.javabin.heroes.TestDataSource;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HeroesRepositoryTest {
    private HeroesRepository heroesRepository;
    private static Random random = new Random();
    private DbContextConnection context;

    @Before
    public void setupDataSource() {
        DataSource dataSource = TestDataSource.createDataSource();
        DbContext dbContext = new DbContext();
        heroesRepository = new HeroesRepository(dbContext);
        context = dbContext.startConnection(dataSource);
    }

    @After
    public void closeConnection() {
        context.close();
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
        hero.setConsentId(random.nextLong() % 1000L);
        hero.setConsentClientIp(randomIpAddress());
        hero.setConsentedAt(randomInstant());
        heroesRepository.updateConsent(hero.getId(), hero.getConsentId(), hero.getConsentClientIp(), hero.getConsentedAt());
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
        hero.setTwitter(hero.getName().toLowerCase() + random.nextInt(10000));
        return hero;
    }

    private static void setConsent(Hero hero) {
        hero.setConsentId(random.nextLong() % 1000L);
        hero.setConsentedAt(randomInstant());
        hero.setConsentClientIp(randomIpAddress());
    }

    private static Hero basicHero() {
        Hero hero = new Hero();
        hero.setEmail(sampleEmail());
        hero.setName(sampleName());
        hero.setAvatarImage(sampleImage());
        return hero;
    }

    private static String sampleImage() {
        return "https://image.example.com/" + UUID.randomUUID() + ".png";
    }

    private static String sampleName() {
        return pickOne(new String[] {
                "Olivia", "Sophia", "Emma", "Isabella", "Elizabeth", "Madison", "Jessica",
                "Oliver", "Ethan", "Alexander", "Michael", "William", "Jacob", "Thomas",
        });
    }

    private static Instant randomInstant() {
        return Instant.now().minusSeconds(random.nextInt(7 * 24 * 60 * 60)).truncatedTo(ChronoUnit.SECONDS);
    }

    private static String randomIpAddress() {
        return random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
    }

    private static String pickOne(String[] examples) {
        return examples[random.nextInt(examples.length)];
    }

    private static String sampleEmail() {
        return "my+email+" + UUID.randomUUID() + "@example.com";
    }
}
