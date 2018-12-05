package no.javabin.heroes;

import javax.servlet.ServletException;
import javax.sql.DataSource;

public class HeroesApiServlet extends ApiServlet {

    private final DataSource datasource;
    private final HeroesContext heroesContext;

    public HeroesApiServlet(DataSource datasource, HeroesContext heroesContext) {
        this.datasource = datasource;
        this.heroesContext = heroesContext;
    }

    @Override
    public void init() throws ServletException {
        registerController(new ProfileController(heroesContext));
    }


}
