package no.javabin.infrastructure.http.server.json;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.javabin.infrastructure.http.server.meta.HttpParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpRequestParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpResponseValueMapping;
import no.javabin.infrastructure.http.server.meta.HttpReturnMapping;
import org.jsonbuddy.JsonNode;
import org.jsonbuddy.parse.JsonParser;
import org.jsonbuddy.pojo.JsonGenerator;

@Retention(RUNTIME)
@Target({PARAMETER, METHOD})
@HttpParameterMapping(JsonBody.JsonBodyMapper.class)
@HttpReturnMapping(JsonBody.JsonBodyMapper.class)
public @interface JsonBody {

    public static class JsonBodyMapper implements HttpRequestParameterMapping, HttpResponseValueMapping {

        private static HttpResponseValueMapping writeJsonNode = (o, resp) -> {
            resp.setContentType("application/json");
            ((JsonNode) o).toJson(resp.getWriter());
        };

        private static HttpResponseValueMapping writePojo = (o, resp) -> {
            resp.setContentType("application/json");
            JsonGenerator.generate(o).toJson(resp.getWriter());
        };

        private HttpResponseValueMapping responseMapping;

        public JsonBodyMapper(JsonBody jsonBody, Class<?> returnType) {
            // Used by HttpReturnMapping
            if (!JsonNode.class.isAssignableFrom(returnType)) {
                this.responseMapping = writePojo;
            } else {
                this.responseMapping = writeJsonNode;
            }
        }

        public JsonBodyMapper(JsonBody jsonBody, Parameter parameter) {
            if (!JsonNode.class.isAssignableFrom(parameter.getType())) {
                throw new IllegalArgumentException(parameter + " must be a JSON type");
            }
        }

        @Override
        public Object apply(HttpServletRequest req, Map<String, String> u) throws IOException {
            return JsonParser.parseToObject(req.getReader());
        }

        @Override
        public void accept(Object o, HttpServletResponse resp) throws IOException {
            this.responseMapping.accept(o, resp);
        }

    }
}

