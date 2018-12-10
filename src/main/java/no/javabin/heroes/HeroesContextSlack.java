package no.javabin.heroes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import no.javabin.infrastructure.configuration.ApplicationProperties;
import no.javabin.infrastructure.http.HttpUrl;
import no.javabin.infrastructure.http.server.HttpRequestException;
import org.flywaydb.core.Flyway;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeroesContextSlack implements HeroesContext {

    private static final Logger logger = LoggerFactory.getLogger(HeroesContextSlack.class);

    private final ApplicationProperties property;

    public HeroesContextSlack(ApplicationProperties propertySource) {
        this.property = propertySource;
    }

    @Override
    public HttpUrl createAuthorizationUrl(String state, boolean admin) {
        List<String> scopes = new ArrayList<>(Arrays.asList("groups:read", "channels:read", "users.profile:read"));
        if (admin) {
            scopes.add("users:read");
            scopes.add("users:read.email");
        }
        return new HttpUrl(String.format("https://%s.slack.com/oauth", getSlackTeam()))
                .addParameter("client_id", getClientId())
                .addParameter("redirect_uri", getRedirectUri())
                .addParameter("state", state)
                .addParameter("scope", String.join(",", scopes));
    }


    private JsonObject exchangeCodeForToken(String code) throws IOException {
        String tokenEndpoint = "https://slack.com/api/oauth.access";
        HttpUrl tokenRequest = new HttpUrl(tokenEndpoint)
                .addParameter("client_id", getClientId())
                .addParameter("redirect_uri", getRedirectUri())
                .addParameter("client_secret", getClientSecret())
                .addParameter("code", code);
        logger.debug("Fetching profile from {}", tokenEndpoint);
        return JsonParser.parseToObject(tokenRequest.toURL());
    }

    private String getSlackTeam() {
        return property.required("oauth2.slack_team");
    }

    public String getRedirectUri() {
        return property.required("oauth2.redirect_uri");
    }

    public String getClientId() {
        return property.required("oauth2.client_id");
    }

    public String getClientSecret() {
        return property.required("oauth2.client_secret");
    }

    @Override
    public Profile exchangeCodeForProfile(String code) throws IOException {
        JsonObject tokenResponse = exchangeCodeForToken(code);
        if (tokenResponse.containsKey("error")) {
            logger.error("Token request failed: {}", tokenResponse);
            throw new HttpRequestException(500, "Failed to authenticate client");
        }


        return new SlackProfile(tokenResponse);
    }

    public int getHttpPort() {
        return Optional.ofNullable(System.getenv("HTTP_PLATFORM_PORT"))
                .map(Integer::parseInt)
                .orElse(9093);
    }

    private Map<String, DataSource> dataSourceCache = new HashMap<>();

    @Override
    public DataSource getDataSource() {
        if (System.getenv("SQLAZURECONNSTR_HEROES_DB_CONNECTION") != null) {
            Map<String, String> connectionProperties = new HashMap<>();
            for (String property : System.getenv("SQLAZURECONNSTR_HEROES_DB_CONNECTION").split(";")) {
                int equalsPos = property.indexOf("=");
                connectionProperties.put(property.substring(0, equalsPos), property.substring(equalsPos+1));
            }

            Matcher dataSourceMatch = Pattern.compile("tcp:([^,]*),(\\d+)").matcher(connectionProperties.get("Data Source"));
            if (!dataSourceMatch.matches()) {
                throw new IllegalArgumentException("Can't parse " + connectionProperties.get("Data Source"));
            }
            String host = dataSourceMatch.group(1);
            String port = dataSourceMatch.group(2);
            String database = connectionProperties.get("Initial Catalog");
            String username = connectionProperties.get("User ID");
            String password = connectionProperties.get("Password");

            String url = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database;


            return getConnection(url, username, password, Optional.of("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
        } else {
            String prefix = "heroes";
            String url = property.property(prefix + ".datasource.url").orElse("jdbc:h2:mem:" + prefix);
            String username = property.property(prefix + ".datasource.username").orElse(prefix);
            String password = property.property(prefix + ".datasource.password").orElse(prefix);
            Optional<String> optDriverClassName = property.property(prefix + ".datasource.driverClassName");

            return getConnection(url, username, password, optDriverClassName);
        }
    }

    public DataSource getConnection(String url, String username, String password, Optional<String> optDriverClassName) {
        String cacheKey = url + "|" + username + "|" + password;

        return dataSourceCache.computeIfAbsent(cacheKey, key -> {
            HikariConfig config = new HikariConfig();

            optDriverClassName
                .ifPresent(driverClassName -> config.setDriverClassName(driverClassName));

            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            HikariDataSource dataSource = new HikariDataSource(config);

            Flyway flyway = Flyway.configure().dataSource(dataSource).load();
            flyway.clean();
            flyway.migrate();

            return dataSource;
        });
    }
}
