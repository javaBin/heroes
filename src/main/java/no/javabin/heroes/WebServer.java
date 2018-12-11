package no.javabin.heroes;

import java.io.File;
import java.util.Optional;

import no.javabin.heroes.api.HeroesApiServlet;
import no.javabin.heroes.slack.HeroesContextSlack;
import no.javabin.infrastructure.configuration.ApplicationProperties;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private Server server;

    private HeroesContextSlack context;

    public WebServer(ApplicationProperties applicationProperties) {
        context = new HeroesContextSlack(applicationProperties);
    }

    public static void main(String[] argv) throws Exception {
        try {
            new WebServer(new ApplicationProperties(System.getenv("PROFILES"))).start();
        } catch (Exception e) {
            logger.error("Failed to start server", e);
        }
    }

    protected void start() throws Exception {
        server = new Server(context.getHttpPort());
        server.addLifeCycleListener(AbstractLifeCycle.STOP_ON_FAILURE);
        server.setHandler(withLogging(createWebAppContext()));
        server.start();

        logger.warn("Started on {}", Optional.ofNullable(System.getenv("WEBSITE_HOSTNAME")).orElseGet(() -> server.getURI().toString()));
    }

    private Handler withLogging(WebAppContext webApp) {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setHandler(webApp);
        requestLogHandler.setRequestLog(new Slf4jRequestLog());
        return requestLogHandler;
    }

    protected WebAppContext createWebAppContext() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.getSessionHandler().setMaxInactiveInterval(30);
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

    static boolean isDevEnviroment() {
        return new File("pom.xml").exists();
    }

}
