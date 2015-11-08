package no.javabin.heroes;

import org.postgresql.ds.PGPoolingDataSource;

public class Postgres {
    private static volatile PGPoolingDataSource source;

    private Postgres() {
    }

    public static synchronized PGPoolingDataSource datasource() {
        if (source != null) {
            return source;
        }
        source = new PGPoolingDataSource();
        source.setDataSourceName("Postgres Data source");
        source.setServerName(Configuration.dbServer());
        source.setDatabaseName(Configuration.dbName());
        source.setUser(Configuration.dbUser());
        source.setPassword(Configuration.dbPassword());
        source.setMaxConnections(10);
        return source;
    }

}
