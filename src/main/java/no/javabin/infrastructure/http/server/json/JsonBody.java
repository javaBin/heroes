package no.javabin.infrastructure.http.server.json;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.javabin.infrastructure.http.server.meta.HttpParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpRequestParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpResponseValueMapping;
import no.javabin.infrastructure.http.server.meta.HttpReturnMapping;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.parse.JsonParser;

@Retention(RUNTIME)
@Target({PARAMETER, METHOD})
@HttpParameterMapping(JsonBodyMapper.class)
@HttpReturnMapping(JsonBodyMapper.class)
public @interface JsonBody {

}

class JsonBodyMapper implements HttpRequestParameterMapping, HttpResponseValueMapping {

    @Override
    public Object apply(HttpServletRequest req, Map<String, String> u) throws IOException {
        return JsonParser.parseToObject(req.getReader());
    }

    @Override
    public void accept(Object o, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        ((JsonNode) o).toJson(resp.getWriter());
    }

}