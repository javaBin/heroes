package no.javabin.infrastructure.http.server.meta;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface HttpRequestParameterMapping {

    Object apply(HttpServletRequest req, Map<String, String> pathParameters) throws IOException;

}