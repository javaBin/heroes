package no.javabin.heroes;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator implements AutoCloseable {
    private ServiceLocator() {

    }

    private static final Map<Long,ServiceLocator> instances = new HashMap<>();

    public static ServiceLocator instance() {
        ServiceLocator serviceLocator = new ServiceLocator();
        ServiceLocator oldVal = instances.put(Thread.currentThread().getId(), serviceLocator);
        if (oldVal != null) {
            throw new TechnicalException("Transaction already running");
        }
        return serviceLocator;
    }

    public Connection connection() {
        return null;
    }

    @Override
    public void close() {
        ServiceLocator remove = instances.remove(Thread.currentThread().getId());
        if (remove != this) {
            throw new TechnicalException("Transaction was not open");
        }
    }
}
