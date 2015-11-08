package no.javabin.heroes;

import org.jsonbuddy.JsonArray;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class DataServlet extends HttpServlet {
    PathComputation pathComputation =  new PathComputation();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        PersonService personService = ServiceLocator.instance().personService();
        switch (Optional.ofNullable(pathComputation.computeGet(pathInfo)).orElse(ServletOperation.UNKNOWN)) {
            case READ_SINGLE_PERSON:
                JsonObject personById = personService.getPersonById(pathInfo.substring(pathInfo.lastIndexOf("/") + 1));
                Optional.ofNullable(personById).orElse(JsonFactory.jsonObject()).toJson(resp.getWriter());
                break;
            case ALL_PERSONS:
                JsonArray allPersons = personService.getAllPersons();
                allPersons.toJson(resp.getWriter());
                break;
            case UNKNOWN:
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (ServiceLocator ignored = ServiceLocator.startThreadContext()) {
            try {
                ignored.setConnection(Postgres.datasource().getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            super.service(req,resp);
        }
    }
}
