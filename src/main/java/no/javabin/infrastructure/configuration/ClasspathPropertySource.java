package no.javabin.infrastructure.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import no.javabin.infrastructure.ExceptionUtil;

public class ClasspathPropertySource implements PropertySource {

    private String name;
    private Properties properties = new Properties();

    public ClasspathPropertySource(String name) {
        URL url = getClass().getResource(name);
        this.name = url != null ? url.toString() : (name + " (not found)");
        try (InputStream inputStream = getClass().getResourceAsStream(name)) {
            if (inputStream == null) {
                return;
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + name + "}";
    }

}
