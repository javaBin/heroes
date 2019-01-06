package no.javabin.heroes.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import no.javabin.heroes.Profile;
import no.javabin.heroes.hero.HeroesContext;
import no.javabin.infrastructure.http.server.ApiServlet;

public class HeroesApiServlet extends ApiServlet {

    private final HeroesContext heroesContext;

    public HeroesApiServlet(HeroesContext heroesContext) {
        this.heroesContext = heroesContext;
    }

    @Override
    public void init() throws ServletException {
        registerController(new LoginController(heroesContext));
        registerController(new AdminController(heroesContext));
        registerController(new ProfileController(heroesContext));
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
