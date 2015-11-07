package no.javabin.heroes;

import org.jsonbuddy.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class DataServlet extends HttpServlet {
    PathComputation pathComputation =  new PathComputation();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        switch (Optional.ofNullable(pathComputation.computeGet(pathInfo)).orElse(ServletOperation.UNKNOWN)) {
            case READ_SINGLE_PERSON:
                PersonService personService = ServiceLocator.instance().personService();
                JsonObject personById = personService.getPersonById(pathInfo.substring(pathInfo.lastIndexOf("/") + 1));
                personById.toJson(resp.getWriter());
                break;
            case UNKNOWN:
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (ServiceLocator ignored = ServiceLocator.startThreadContext()) {
            super.service(req,resp);
        }
    }
}
