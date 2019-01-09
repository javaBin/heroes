package no.javabin.heroes.hero.achievement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Year;
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
                .put("type", Achievement.FOREDRAGSHOLDER_JZ)
                .put("year", "2018")
                .put("title", "How to write Java applications"));

        assertThat(heroesRepository.retrieveById(hero.getId()).getAchievements())
            .extracting("label")
            .contains("Foredragsholder JavaZone 2018: How to write Java applications");
    }

    @Test
    public void shouldDeleteAchievement() {
        UUID achievementId = achievementRepository.add(hero.getId(), new JsonObject()
                .put("type", Achievement.STYRE)
                .put("year", "2017")
                .put("role", BoardMemberRole.CHAIR));

        achievementRepository.delete(hero.getId(), achievementId);
        assertThat(heroesRepository.retrieveById(hero.getId()).getAchievements())
            .extracting("id")
            .doesNotContain(achievementId);
    }

    @Test
    public void shouldUpdateAchievement() {
        String title = "Title " + UUID.randomUUID();
        UUID achievementId = achievementRepository.add(hero.getId(), new JsonObject()
                .put("type", Achievement.FOREDRAGSHOLDER_JAVABIN)
                .put("date", "2018-11-01")
                .put("title", title));

        achievementRepository.update(hero.getId(), achievementId, new JsonObject()
                .put("type", Achievement.FOREDRAGSHOLDER_JAVABIN)
                .put("title", "Updated title"));

        assertThat(heroesRepository.retrieveById(hero.getId()).getAchievements())
            .extracting("label")
            .contains("Foredragsholder JavaBin 1. november 2018: Updated title");
    }

    @Test
    public void shouldListAllAchivementTypes() {
        ConferenceSpeakerAchievement confSpeaker = new ConferenceSpeakerAchievement();
        confSpeaker.setYear(Year.of(2017));
        confSpeaker.setTitle("This is my talk");
        achievementRepository.add(hero.getId(), confSpeaker);
        assertThat(confSpeaker).hasNoNullFieldsOrPropertiesExcept("label", "data", "type");

        UsergroupSpeakerAchievement usergroupSpeaker = new UsergroupSpeakerAchievement();
        usergroupSpeaker.setDate(LocalDate.of(2017, 11, 15));
        usergroupSpeaker.setTitle("I want to share");
        achievementRepository.add(hero.getId(), usergroupSpeaker);
        assertThat(usergroupSpeaker).hasNoNullFieldsOrPropertiesExcept("label", "data", "type");

        BoardMemberAchievement boardMember = new BoardMemberAchievement();
        boardMember.setYear(Year.of(2017));
        boardMember.setRole(BoardMemberRole.CHAIR);
        achievementRepository.add(hero.getId(), boardMember);
        assertThat(boardMember).hasNoNullFieldsOrPropertiesExcept("label", "data", "type");

        assertThat(achievementRepository.listAchievements(hero.getId()))
            .contains(confSpeaker, usergroupSpeaker, boardMember);
        assertThat(achievementRepository.listAchievements(hero.getId()))
            .filteredOn(a -> a.getId().equals(confSpeaker.getId()))
            .first()
            .isEqualToComparingFieldByField(confSpeaker);
    }

}
