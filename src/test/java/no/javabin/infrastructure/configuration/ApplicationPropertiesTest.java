package no.javabin.infrastructure.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class ApplicationPropertiesTest {

    private File propertiesDir = new File("target/test-properties/" + UUID.randomUUID());

    @Test
    public void shouldFindExistingProperties() throws IOException {
        Files.write(new File(propertiesDir, "application.properties").toPath(), "hello=world\n".getBytes());
        ApplicationProperties properties = new ApplicationProperties(new PropertySourceList(propertiesDir, new String[] { "test" }));
        assertThat(properties.required("hello")).isEqualTo("world");
    }

    @Test
    public void shouldShowMessageOnMissingProperty() throws IOException {
        Files.write(new File(propertiesDir, "application.properties").toPath(), "hello=world\n".getBytes());
        ApplicationProperties properties = new ApplicationProperties(new PropertySourceList(propertiesDir, new String[] { "test" }));

        assertThatThrownBy(() -> properties.required("missingProperty"))
            .hasMessageContaining("Missing property [missingProperty]")
            .hasMessageContaining(propertiesDir + System.getProperty("file.separator") + "application.properties}")
            .hasMessageContaining(propertiesDir + System.getProperty("file.separator") + "application-test.properties (not found)}")
            ;

    }


    @Before
    public void createPropertiesDir() throws IOException {
        Files.createDirectories(propertiesDir.toPath());
    }

}
