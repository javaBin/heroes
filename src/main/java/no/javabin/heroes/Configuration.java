package no.javabin.heroes;

import java.util.Optional;

public class Configuration {

    private static String readEnviromentVariable(String key,String defaultValue) {
        return Optional.ofNullable(System.getProperty(key))
            .filter(s -> !s.isEmpty())
            .orElse(defaultValue);
    }

    public static int serverPort() {
        return Integer.parseInt(readEnviromentVariable("PORT","9093"));
    }
}
