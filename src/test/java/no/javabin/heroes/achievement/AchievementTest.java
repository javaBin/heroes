package no.javabin.heroes.achievement;

import no.javabin.heroes.DataServlet;
import no.javabin.heroes.InMemoryDbTest;
import no.javabin.heroes.ServiceLocator;
import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AchievementTest extends InMemoryDbTest {
    @Test
    public void shouldAddAndReadAchivement() throws Exception {
        JsonObject createAch = JsonFactory.jsonObject()
                .put("name", "Major Ach")
                .put("description", "Who cares");
        JsonObject createResult = ServiceLocator.instance().achievementService().insertAchievement(createAch);
        JsonObject another = JsonFactory.jsonObject()
                .put("name", "Another Ach")
                .put("description", "Who cares");
        ServiceLocator.instance().achievementService().insertAchievement(another);

        String id = createResult.requiredString("id");

        assertThat(id).isNotNull();

        JsonObject achievementById = ServiceLocator.instance().achievementService().getAchievementById(id);

        assertThat(achievementById).isEqualTo(createResult);

        JsonArray allAchievements = ServiceLocator.instance().achievementService().getAllAchievements();

        assertThat(allAchievements.size()).isEqualTo(2);


    }
}