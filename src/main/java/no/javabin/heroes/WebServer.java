package no.javabin.heroes;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGPoolingDataSource;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.sql.DataSource;

public class WebServer {
    private static Server server;

    public static void main(String[] argv) throws Exception {
        setConfigFile(argv);
        new WebServer().start();
    }

    public static void setConfigFile(String[] argv) {
        Locale.setDefault(new Locale("no"));
        if (argv != null && argv.length > 0) {
//            System.setProperty(Configuration.CONFIG_FILE_PROPERTY, argv[0]);
        }
    }


    protected void start() throws Exception {
        //Locale.setDefault(new Locale(Configuration.getLocale()));
        PGPoolingDataSource datasource = Postgres.datasource();
        migrateDb(datasource);
        server = new Server(Configuration.serverPort());
        server.setHandler(getHandler(datasource));
        server.start();

        System.out.println(server.getURI() + " at " + LocalDateTime.now());
        System.out.println("Path=" + new File(".").getAbsolutePath());
    }

    protected void migrateDb() {
        migrateDb(Postgres.datasource());
    }

    protected void migrateDb(PGPoolingDataSource datasource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(datasource);
        flyway.migrate();

    }


    protected WebAppContext getHandler(DataSource datasource) {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(30);
        webAppContext.setContextPath("/");

        if (isDevEnviroment()) {
            // Development ie running in ide
            webAppContext.setResourceBase("src/main/resources/webapp/dist");
        } else {
            // Prod ie running from jar
            webAppContext.setBaseResource(Resource.newClassPathResource("webapp/dist", true, false));
        }


        webAppContext.addServlet(new ServletHolder(new DataServlet()), "/data/*");
        HeroesContext heroesContext = new HeroesContext(new ApplicationProperties(System.getenv("PROFILES")));
        webAppContext.addServlet(new ServletHolder(new HeroesApiServlet(datasource, heroesContext)), "/api/*");
        return webAppContext;
    }



    @SuppressWarnings("UnusedDeclaration")
    protected void stop() throws Exception {
        server.stop();
    }

    static boolean isDevEnviroment() {
        return new File("pom.xml").exists();
    }

}
