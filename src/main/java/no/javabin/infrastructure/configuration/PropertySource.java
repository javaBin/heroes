package no.javabin.infrastructure.configuration;

import java.util.Optional;

public interface PropertySource {

    Optional<String> get(String key);

}
