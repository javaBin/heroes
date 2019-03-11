package no.javabin.heroes.api;

import no.javabin.heroes.HeroesMarkers;
import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.HeroesContext;
import org.actioncontroller.ApiServlet;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class HeroesApiServlet extends ApiServlet {

    private final HeroesContext heroesContext;
    private DbContext dbContext;

    public HeroesApiServlet(HeroesContext heroesContext) {
        this.heroesContext = heroesContext;
        this.dbContext = new DbContext();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(DbContextConnection ignored = dbContext.startConnection(heroesContext.getDataSource())) {
            try (
                    MDC.MDCCloseable ignored1 = MDC.putCloseable("path", req.getRequestURI());
                    MDC.MDCCloseable ignored2 = MDC.putCloseable("user", getUserName(req));
            ) {
                try {
                    super.service(req, resp);
                } catch (RuntimeException e) {
                    LoggerFactory.getLogger(getClass()).warn(HeroesMarkers.OPS, "Error while processing request", e);
                    resp.sendError(500, e.getMessage());
                }
            }
        }
    }

    private String getUserName(HttpServletRequest req) {
        return Optional.ofNullable(getProfile(req)).map(Profile::getUsername).orElse(null);
    }

    @Override
    public void init() {
        registerController(new PublicController(heroesContext, dbContext));
        registerController(new AdminController(dbContext));
        registerController(new ProfileController(dbContext));
    }

    @Override
    protected boolean isUserLoggedIn(HttpServletRequest req) {
        return getProfile(req) != null;
    }

    @Override
    protected boolean isUserInRole(HttpServletRequest req, String role) {
        if (role.equals("admin")) {
            return getProfile(req).hasAdminScope();
        }
        return false;
    }

    private Profile getProfile(HttpServletRequest req) {
        return (Profile) req.getSession().getAttribute("profile");
    }
}
