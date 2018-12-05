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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.javabin.heroes.HttpRequestException;
import org.jsonbuddy.JsonNode;

public class ApiServlet extends HttpServlet {

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
        ApiRoute route = findRoute(req, pathParameters);

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
            resp.sendError(404);
        }
    }

    private void sendResponse(Object result, HttpServletResponse resp) throws IOException {
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

    private ApiRoute findRoute(HttpServletRequest req, Map<String, String> pathParameters) {
        for (Object controller : controllers) {
            for (Method method : controller.getClass().getMethods()) {
                pathParameters.clear();
                Get annotation = method.getAnnotation(Get.class);
                if (annotation != null && pathMatches(annotation.value(), req.getPathInfo(), pathParameters)) {
                    return new ApiRoute(controller,  method);
                }
            }
        }
        return null;
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


    public Object[] createArguments(Method method, HttpServletRequest req, Map<String, String> pathParameters) {
        Object[] arguments = new Object[method.getParameterCount()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = createArgument(method, i, req, pathParameters);
        }
        return arguments;
    }

    public Object createArgument(Method method, int i, HttpServletRequest req, Map<String, String> pathParameters) {
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
                return pathParameters.get(pathParam.value());
            } else if ((sessionParam = parameter.getAnnotation(SessionParameter.class)) != null) {
                return req.getSession().getAttribute(sessionParam.value());
            } else if ((reqParam = parameter.getAnnotation(RequestParam.class)) != null) {
                return req.getParameter(reqParam.value());
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
