package no.javabin.heroes;

import java.util.Optional;

public class Configuration {

    private static String readEnviromentVariable(String key,String defaultValue) {
        return Optional.ofNullable(System.getProperty(key))
            .orElse(defaultValue);
    }

    public static int serverPort() {
        return Integer.parseInt(readEnviromentVariable("PORT","9093"));
    }

    public static String dbName() {
        return readEnviromentVariable("DB_NAME","javabinheroes");
    }

    public static String dbUser() {
        return readEnviromentVariable("DB_USERNAME","postgres");
    }

    public static String dbPassword() {
        return readEnviromentVariable("DB_PASSWORD","bingo");
    }

    public static String dbServer() {
        return readEnviromentVariable("DB_SERVER", "localhost");
    }
}
