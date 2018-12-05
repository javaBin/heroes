package no.javabin.heroes;

import java.util.Optional;

public interface PropertySource {

    Optional<String> get(String key);

}
