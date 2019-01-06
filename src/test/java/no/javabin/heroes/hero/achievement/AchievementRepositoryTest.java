package no.javabin.heroes.hero.achievement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import javax.sql.DataSource;

import no.javabin.heroes.TestDataSource;
import no.javabin.heroes.hero.Hero;
import no.javabin.heroes.hero.HeroesRepository;
import no.javabin.heroes.hero.HeroesRepositoryTest;
import org.jsonbuddy.JsonObject;
import org.junit.Before;
import org.junit.Test;

public class AchievementRepositoryTest {

    private HeroesRepository heroesRepository;
    private AchievementRepository achievementRepository;
    private Hero hero;

    @Before
    public void setupDataSource() {
        DataSource dataSource = TestDataSource.createDataSource();
        heroesRepository = new HeroesRepository(() -> dataSource);
        achievementRepository = new AchievementRepository(() -> dataSource);

        hero = HeroesRepositoryTest.sampleHero();
        heroesRepository.save(hero);
    }

    @Test
    public void shouldSaveNewTalkAchievement() {
        achievementRepository.add(hero.getId(), new JsonObject()
                .put("type", "foredragsholder_jz")
                .put("year", "2018")
                .put("title", "How to write Java applications"));

        assertThat(heroesRepository.retrieveById(hero.getId()).getAchievements())
            .extracting("label")
            .contains("Foredragsholder JavaZone 2018: How to write Java applications");
    }

    @Test
    public void shouldDeleteAchievement() {
        achievementRepository.add(hero.getId(), new JsonObject()
                .put("type", "styremedlem")
                .put("year", "2017")
                .put("role", "styreleder"));

        UUID achievementId = heroesRepository.retrieveById(hero.getId()).getAchievements()
            .stream().filter(a -> a.getData().requiredString("type").equals("styremedlem")).map(a -> a.getId()).findAny().get();

        achievementRepository.delete(hero.getId(), achievementId);
        assertThat(heroesRepository.retrieveById(hero.getId()).getAchievements())
            .extracting("id")
            .doesNotContain(achievementId);
    }

    @Test
    public void shouldUpdateAchievement() {
        String title = "Title " + UUID.randomUUID();
        achievementRepository.add(hero.getId(), new JsonObject()
                .put("type", "foredragsholder_javabin")
                .put("date", "2018-11-01")
                .put("title", title));

        UUID achievementId = heroesRepository.retrieveById(hero.getId()).getAchievements()
                .stream().filter(a -> a.getData().requiredString("title").equals(title)).map(a -> a.getId()).findAny().get();

        achievementRepository.update(hero.getId(), achievementId, new JsonObject()
                .put("title", "Updated title"));

        assertThat(heroesRepository.retrieveById(hero.getId()).getAchievements())
            .extracting("label")
            .contains("Foredragsholder JavaBin 1. november 2018: Updated title");
    }

}
