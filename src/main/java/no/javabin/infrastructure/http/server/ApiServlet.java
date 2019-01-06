package no.javabin.infrastructure.http.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.javabin.infrastructure.http.Delete;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(ApiServlet.class);

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


    protected boolean isUserLoggedIn(HttpServletRequest req) {
        return req.getRemoteUser() != null;
    }

    protected boolean isUserInRole(HttpServletRequest req, String role) {
        return req.isUserInRole(role);
    }

    private List<Object> controllers = new ArrayList<>();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Function<Method, Optional<String>>> actionMatchers = new HashMap<>();

        actionMatchers.put("GET", method -> Optional.ofNullable(method.getAnnotation(Get.class)).map(Get::value));
        actionMatchers.put("POST", method -> Optional.ofNullable(method.getAnnotation(Post.class)).map(Post::value));
        actionMatchers.put("PUT", method -> Optional.ofNullable(method.getAnnotation(Put.class)).map(Put::value));
        actionMatchers.put("DELETE", method -> Optional.ofNullable(method.getAnnotation(Delete.class)).map(Delete::value));

        Map<String, String> pathParameters = new HashMap<>();
        ApiRoute route = findRoute(req, pathParameters, actionMatchers.get(req.getMethod()));

        if (route == null) {
            logger.warn("No route for {}", req.getPathInfo());
            resp.sendError(404);
            return;
        }
        if (!verifyUserPermissions(req, resp, route)) {
            return;
        }

        Object result;
        try {
            Object[] arguments = createArguments(route.getAction(), req, pathParameters);
            result = invoke(route.getController(), route.getAction(), arguments);
        } catch (HttpRequestException e) {
            if (e.getStatusCode() >= 500) {
                logger.error("While serving {}", route, e);
            } else {
                logger.info("While serving {}", route, e);
            }
            resp.sendError(e.getStatusCode(), e.getMessage());
            return;
        }
        sendResponse(result, resp);
    }

    protected boolean verifyUserPermissions(HttpServletRequest req, HttpServletResponse resp, ApiRoute route) throws IOException {
        Method action = route.getAction();
        RequireUserRole requireUserRole = action.getDeclaredAnnotation(RequireUserRole.class);
        if (requireUserRole != null) {
            if (!isUserLoggedIn(req)) {
                logger.warn("User must be logged in for {}", action);
                resp.setStatus(401);
                resp.setContentType("application/json");
                resp.getWriter().write(new JsonObject().put("message", "Login required").toJson());
                return false;
            }
            if (!isUserInRole(req, requireUserRole.value())) {
                logger.warn("User failed to authenticate for {}: Missing role {} for user", action, requireUserRole.value());
                resp.setStatus(403);
                resp.setContentType("application/json");
                resp.getWriter().write(new JsonObject().put("message", "Insufficient permissions").toJson());
                return false;
            }
        }
        return true;
    }


    private void sendResponse(Object result, HttpServletResponse resp) throws IOException {
        if (result == null) {
            return;
        }
        if (result instanceof URL) {
            resp.sendRedirect(result.toString());
            return;
        }
        if (result instanceof CharSequence) { // And annotation @SendRedirect
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
            if ((pathParam = parameter.getAnnotation(PathParam.class)) != null) {
                String result = pathParameters.get(pathParam.value());
                if (result == null) {
                    throw new HttpRequestException(500, "Path parameter :" + pathParam.value() + " not matched for " + method);
                }
                if (parameter.getType() == String.class) {
                    return result;
                } else if (parameter.getType() == UUID.class) {
                    return UUID.fromString(result);
                } else {
                    throw new HttpRequestException(500, "Illegal type for " + pathParam.value());
                }
            } else if ((sessionParam = parameter.getAnnotation(SessionParameter.class)) != null) {
                if (parameter.getType() == Consumer.class) {
                    return new Consumer<Object>() {
                        @Override
                        public void accept(Object o) {
                            if (sessionParam.invalidate()) {
                                req.getSession().invalidate();
                            }
                            req.getSession(true).setAttribute(sessionParam.value(), o);
                        }
                    };
                }

                Object value = req.getSession().getAttribute(sessionParam.value());
                if (parameter.getType() == Optional.class) {
                    return Optional.ofNullable(value);
                } else if (value != null) {
                    return value;
                } else {
                    throw new HttpRequestException(401, "Missing required session parameter " + sessionParam.value());
                }
            } else if ((reqParam = parameter.getAnnotation(RequestParam.class)) != null) {
                String value = req.getParameter(reqParam.value());
                boolean optional = parameter.getType() == Optional.class;

                if (value == null) {
                    if (!optional) {
                        throw new HttpRequestException(400, "Missing required parameter " + reqParam.value());
                    }
                    return Optional.empty();
                }

                Type parameterType;
                if (optional) {
                    Type parameterizedType = parameter.getParameterizedType();
                    parameterType = ((ParameterizedType)parameterizedType).getActualTypeArguments()[0];
                } else {
                    parameterType = parameter.getType();
                }

                Object parameterValue = convertParameterType(value, parameterType);
                return optional ? Optional.of(parameterValue) : parameterValue;
            } else if (parameter.getAnnotation(Body.class) != null) {
                // TODO: This isn't very nice if the content-type isn't application/json
                // TODO: This isn't very nice if parameter.getType() == JsonArray.class
                return JsonParser.parseToObject(req.getReader());
            } else if (parameter.getAnnotation(RequestParam.ClientIp.class) != null) {
                // TODO: Skip proxies
                return req.getRemoteAddr();
            } else {
                throw new IllegalArgumentException("Don't know how to get "
                        + method.getDeclaringClass().getSimpleName() + "#" + method.getName()
                        + " parameter " + i + ": of type " + parameter.getType().getSimpleName() + " " + Arrays.asList(parameter.getAnnotations()));
            }
        }
    }

    public Object convertParameterType(String value, Type parameterType) {
        if (parameterType == String.class) {
            return value;
        } else if (parameterType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (parameterType == Integer.class || parameterType == Integer.TYPE) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new HttpRequestException(400,
                        String.format("Invalid parameter amount '%s' is not an %s", value, parameterType));
            }
        } else {
            throw new HttpRequestException(500, "Unhandled parameter type " + parameterType);
        }
    }

    protected void registerController(Object controller) {
        this.controllers.add(controller);
    }

}
