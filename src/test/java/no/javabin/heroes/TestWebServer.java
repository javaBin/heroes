package no.javabin.heroes;

import org.flywaydb.core.Flyway;

public class TestWebServer extends WebServer {
    public static void main(String[] argv) throws Exception {
        setConfigFile(argv);
        new TestWebServer().start();
    }

    protected void migrateDb() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(Postgres.datasource());
        flyway.clean();
        flyway.migrate();
        // TODO Add some test data??
    }


}
