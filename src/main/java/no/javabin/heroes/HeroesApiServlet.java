package no.javabin.heroes;

import javax.servlet.ServletException;
import javax.sql.DataSource;

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


}
