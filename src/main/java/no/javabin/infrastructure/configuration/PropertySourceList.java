package no.javabin.infrastructure.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PropertySourceList implements PropertySource {

    private List<PropertySource> propertySources = new ArrayList<>();

    public PropertySourceList(String profilesString) {
        this(profilesString != null && !profilesString.isEmpty() ? profilesString.split(",") : new String[0]);
    }

    public PropertySourceList(String[] profiles) {
        this(new File("."), profiles);
    }

    public PropertySourceList(File configurationDirectory, String[] profiles) {
        for (String profile : profiles) {
            propertySources.add(new FilePropertySource(new File(configurationDirectory, "application-" + profile + ".properties")));
            propertySources.add(new ClasspathPropertySource("/application-" + profile + ".properties"));
        }
        propertySources.add(new FilePropertySource(new File(configurationDirectory, "application.properties")));
        propertySources.add(new ClasspathPropertySource("/application.properties"));
    }

    @Override
    public Optional<String> get(String key) {
        for (PropertySource propertySource : propertySources) {
            Optional<String> result = propertySource.get(key);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + propertySources;
    }

}
