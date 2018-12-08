package no.javabin.infrastructure.http.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.javabin.heroes.HttpRequestException;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(ApiServlet.class);

    private static class ApiRoute {

        public ApiRoute(Object controller, Method action) {
            this.controller = controller;
            this.action = action;
        }

        private final Object controller;

        private final Method action;

        public Object getController() {
            return controller;
        }

        public Method getAction() {
            return action;
        }
    }


    private List<Object> controllers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> pathParameters = new HashMap<>();
        ApiRoute route = findRoute(req, pathParameters,
                method -> Optional.ofNullable(method.getAnnotation(Get.class)).map(Get::value));

        if (route != null) {
            Object[] arguments = createArguments(route.getAction(), req, pathParameters);
            Object result;
            try {
                result = invoke(route.getController(), route.getAction(), arguments);
            } catch (HttpRequestException e) {
                resp.sendError(e.getStatusCode(), e.getMessage());
                return;
            }
            sendResponse(result, resp);
        } else {
            logger.warn("No route for {]", req.getPathInfo());
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> pathParameters = new HashMap<>();
        ApiRoute route = findRoute(req, pathParameters,
                method -> Optional.ofNullable(method.getAnnotation(Post.class)).map(Post::value));

        if (route == null) {
            logger.warn("No route for {]", req.getPathInfo());
            resp.sendError(400);
            return;
        }

        Object[] arguments = createArguments(route.getAction(), req, pathParameters);
        Object result;
        try {
            result = invoke(route.getController(), route.getAction(), arguments);
        } catch (HttpRequestException e) {
            resp.sendError(e.getStatusCode(), e.getMessage());
            return;
        }
        sendResponse(result, resp);
    }

    private void sendResponse(Object result, HttpServletResponse resp) throws IOException {
        if (result == null) {
            return;
        }
        if (result instanceof URL) {
            resp.sendRedirect(result.toString());
            return;
        }

        if (result instanceof JsonNode) {
            resp.setContentType("application/json");
        }
        resp.getWriter().write(result.toString());
    }

    private Object invoke(Object controller, Method action, Object[] arguments) {
        try {
            return action.invoke(controller, arguments);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            } else {
                e.getTargetException().printStackTrace();
                throw new HttpRequestException(500, e.getTargetException());
            }
        } catch (IllegalAccessException e) {
            throw new HttpRequestException(500, e);
        }
    }

    private ApiRoute findRoute(HttpServletRequest req, Map<String, String> pathParameters, Function<Method, Optional<String>> pattern) {
        for (Object controller : controllers) {
            for (Method method : controller.getClass().getMethods()) {
                pathParameters.clear();
                Optional<String> pathPattern = pattern.apply(method);
                if (pathPattern.isPresent() && pathMatches(pathPattern.get(), req.getPathInfo(), pathParameters)) {
                    return new ApiRoute(controller,  method);
                }
            }
        }
        return null;
    }

    public Optional<String> getPathPattern(Method method) {
        return Optional.ofNullable(method.getAnnotation(Get.class)).map(Get::value);
    }

    public boolean pathMatches(String actionPathPattern, String actualPath, Map<String, String> pathParameters) {
        String[] patternParts = actionPathPattern.split("/");
        String[] actualParts = actualPath.split("/");
        if (patternParts.length != actualParts.length) return false;

        for (int i = 0; i < patternParts.length; i++) {
            if (patternParts[i].startsWith(":")) {
                pathParameters.put(patternParts[i].substring(1), actualParts[i]);
            } else if (!patternParts[i].equals(actualParts[i])) {
                return false;
            }
        }

        return true;
    }


    public Object[] createArguments(Method method, HttpServletRequest req, Map<String, String> pathParameters) throws IOException {
        Object[] arguments = new Object[method.getParameterCount()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = createArgument(method, i, req, pathParameters);
        }
        return arguments;
    }

    public Object createArgument(Method method, int i, HttpServletRequest req, Map<String, String> pathParameters) throws IOException {
        Parameter parameter = method.getParameters()[i];
        if (parameter.getType() == HttpSession.class) {
            return req.getSession();
        } else if (parameter.getType() == HttpServletRequest.class) {
            return req;
        } else {
            PathParam pathParam;
            RequestParam reqParam;
            SessionParameter sessionParam;
            RequestParam.ClientIp clientIp;
            Body body;
            if ((pathParam = parameter.getAnnotation(PathParam.class)) != null) {
                return pathParameters.get(pathParam.value());
            } else if ((sessionParam = parameter.getAnnotation(SessionParameter.class)) != null) {
                Object value = req.getSession().getAttribute(sessionParam.value());
                return parameter.getType() == Optional.class ? Optional.ofNullable(value) : value;
            } else if ((reqParam = parameter.getAnnotation(RequestParam.class)) != null) {
                return req.getParameter(reqParam.value());
            } else if ((body = parameter.getAnnotation(Body.class)) != null) {
                // TODO: This isn't very nice if the content-type isn't application/json
                // TODO: This isn't very nice if parameter.getType() == JsonArray.class
                return JsonParser.parseToObject(req.getReader());
            } else if ((clientIp = parameter.getAnnotation(RequestParam.ClientIp.class)) != null) {
                // TODO: Skip proxies
                return req.getRemoteAddr();
            } else {
                throw new IllegalArgumentException("Don't know how to get "
                        + method.getDeclaringClass().getSimpleName() + "#" + method.getName()
                        + " parameter " + i + ": of type " + parameter.getType().getSimpleName() + " " + Arrays.asList(parameter.getAnnotations()));
            }
        }
    }

    protected void registerController(Object controller) {
        this.controllers.add(controller);
    }

}
