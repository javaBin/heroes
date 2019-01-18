package no.javabin.heroes.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.HeroesContext;
import no.javabin.infrastructure.http.server.ApiServlet;
import org.fluentjdbc.DbContext;
import org.fluentjdbc.DbContextConnection;

public class HeroesApiServlet extends ApiServlet {

    private final HeroesContext heroesContext;
    private DbContext dbContext;

    public HeroesApiServlet(HeroesContext heroesContext) {
        this.heroesContext = heroesContext;
        this.dbContext = new DbContext();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try(DbContextConnection connection = dbContext.startConnection(heroesContext.getDataSource())) {
            super.service(req, resp);
        }
    }

    @Override
    public void init() throws ServletException {
        registerController(new LoginController(heroesContext, dbContext));
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
