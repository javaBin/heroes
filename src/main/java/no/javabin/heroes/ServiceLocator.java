package no.javabin.heroes;

import no.javabin.heroes.achievement.AchievementDao;
import no.javabin.heroes.achievement.AchievementService;
import no.javabin.heroes.person.PersonDao;
import no.javabin.heroes.person.PersonService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator implements AutoCloseable {
    private Connection connection;
    private PersonService personService;
    private AchievementService achievementService;


    private ServiceLocator() {

    }

    private static final Map<Long, ServiceLocator> instances = new HashMap<>();

    public static ServiceLocator startThreadContext() {
        ServiceLocator serviceLocator = new ServiceLocator();
        ServiceLocator oldVal = instances.put(Thread.currentThread().getId(), serviceLocator);
        if (oldVal != null) {
            throw new TechnicalException("Transaction already running");
        }
        return serviceLocator;
    }

    public static ServiceLocator instance() {
        ServiceLocator serviceLocator = instances.get(Thread.currentThread().getId());
        if (serviceLocator == null) {
            throw new TechnicalException("Transaction not started");
        }
        return serviceLocator;
    }

    public Connection connection() {
        return connection;
    }

    public ServiceLocator setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public PersonService personService() {
        if (personService == null) {
            personService = new PersonService(new PersonDao());
        }
        return personService;
    }

    public AchievementService achievementService() {
        if (achievementService == null) {
            achievementService = new AchievementService(new AchievementDao());
        }
        return achievementService;
    }

    public ServiceLocator setPersonService(PersonService personService) {
        this.personService = personService;
        return this;
    }


    @Override
    public void close() {
        ServiceLocator remove = instances.remove(Thread.currentThread().getId());
        if (remove != this) {
            throw new TechnicalException("Transaction was not open");
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            connection = null;
        }
    }
}