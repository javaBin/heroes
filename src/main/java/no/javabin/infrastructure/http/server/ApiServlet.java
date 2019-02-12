package no.javabin.infrastructure.http.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(ApiServlet.class);

    protected boolean isUserLoggedIn(HttpServletRequest req) {
        return req.getRemoteUser() != null;
    }

    protected boolean isUserInRole(HttpServletRequest req, String role) {
        return req.isUserInRole(role);
    }

    private Map<String, List<ApiServletAction>> routes = new HashMap<>();
    {
        routes.put("GET", new ArrayList<>());
        routes.put("POST", new ArrayList<>());
        routes.put("PUT", new ArrayList<>());
        routes.put("DELETE", new ArrayList<>());
    }

    private ApiServletCompositeException controllerException;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> pathParameters = new HashMap<>();
        for (ApiServletAction apiRoute : routes.get(req.getMethod())) {
            if (apiRoute.matches(req.getPathInfo(), pathParameters)) {
                apiRoute.invoke(req, resp, pathParameters, this);
                return;
            }
        }

        logger.warn("No route for {}", req.getPathInfo());
        resp.sendError(404);
    }

    @Override
    public final void init(ServletConfig config) throws ServletException {
        this.controllerException = new ApiServletCompositeException();
        super.init(config);
        if (!controllerException.isEmpty()) {
            throw controllerException;
        }
    }

    protected void registerController(Object controller) {
        try {
            registerActions(controller);
        } catch (ApiControllerCompositeException e) {
            controllerException.addControllerException(e);
        }
    }

    private void registerActions(Object controller) {
        ApiControllerCompositeException exceptions = new ApiControllerCompositeException(controller);
        for (Method method : controller.getClass().getMethods()) {
            try {
                addRoute("GET", Optional.ofNullable(method.getAnnotation(Get.class)).map(a -> a.value()),
                        controller, method);
                addRoute("POST", Optional.ofNullable(method.getAnnotation(Post.class)).map(a -> a.value()),
                        controller, method);
                addRoute("PUT", Optional.ofNullable(method.getAnnotation(Put.class)).map(a -> a.value()),
                        controller, method);
                addRoute("DELETE", Optional.ofNullable(method.getAnnotation(Delete.class)).map(a -> a.value()),
                        controller, method);
            } catch (ApiServletException e) {
                exceptions.addActionException(e);
            }
        }
        if (!exceptions.isEmpty()) {
            throw exceptions;
        }
    }

    private void addRoute(String httpMethod, Optional<Object> path, Object controller, Method actionMethod) {
        path.ifPresent(p -> routes.get(httpMethod).add(new ApiServletAction(controller, actionMethod, p.toString())));

    }
}
