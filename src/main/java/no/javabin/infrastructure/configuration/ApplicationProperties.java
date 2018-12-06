package no.javabin.infrastructure.configuration;

public class ApplicationProperties {

    private final PropertySource properties;

    public ApplicationProperties(String profiles) {
        this(new PropertySourceList(profiles));
    }

    public ApplicationProperties(PropertySource properties) {
        this.properties = properties;
    }

    public String required(String key) {
        return properties.get(key)
                .orElseThrow(() -> new IllegalStateException("Missing property [" + key + "] in " + properties));
    }

}
