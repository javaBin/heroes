package no.javabin.heroes;

import java.io.File;
import no.javabin.infrastructure.configuration.ApplicationProperties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static Server server;

    private HeroesContextSlack context;

    public WebServer(ApplicationProperties applicationProperties) {
        context = new HeroesContextSlack(applicationProperties);
    }

    public static void main(String[] argv) throws Exception {
        new WebServer(new ApplicationProperties(System.getenv("PROFILES"))).start();
    }

    protected void start() throws Exception {
        //Locale.setDefault(new Locale(Configuration.getLocale()));
        server = new Server(context.getHttpPort());
        server.setHandler(createWebAppContext());
        server.start();

        logger.warn("Started on {}", server.getURI());
    }

    protected WebAppContext createWebAppContext() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(30);
        webAppContext.setContextPath("/");

        if (isDevEnviroment()) {
            // Development ie running in ide
            webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
            webAppContext.setResourceBase("src/main/resources/webapp/dist");
        } else {
            // Prod ie running from jar
            webAppContext.setBaseResource(Resource.newClassPathResource("webapp/dist", true, false));
        }


        webAppContext.addServlet(new ServletHolder(new HeroesApiServlet(this.context)), "/api/*");
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
