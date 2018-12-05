package no.javabin.heroes;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGPoolingDataSource;

public class TestWebServer extends WebServer {
    public static void main(String[] argv) throws Exception {
        setConfigFile(argv);
        new TestWebServer().start();
    }

    protected void migrateDb() {
        migrateDb(Postgres.datasource());
    }

    protected void migrateDb(PGPoolingDataSource datasource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(Postgres.datasource());
        flyway.clean();
        flyway.migrate();
        // TODO Add some test data??
    }


}
