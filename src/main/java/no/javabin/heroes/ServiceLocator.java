package no.javabin.heroes;

import java.sql.Connection;

public class ServiceLocator {
    private ServiceLocator() {

    }

    public static ServiceLocator instance() {
        return null;
    }

    public Connection connection() {
        return null;
    }
}
