package no.javabin.heroes;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemSetupTest extends InMemoryDbTest {
    @Test
    public void shouldHaveConnection() throws Exception {
        assertThat(ServiceLocator.instance().connection()).isNotNull();

    }
}
