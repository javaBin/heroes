package no.javabin.infrastructure.http.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import no.javabin.infrastructure.http.server.meta.AbstractHttpRequestParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpParameterMapping;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@HttpParameterMapping(PathParameterMapping.class)
public @interface PathParam {

    String value();

}


class PathParameterMapping extends AbstractHttpRequestParameterMapping {

    private PathParam pathParam;

    public PathParameterMapping(PathParam pathParam, Parameter parameter) {
        super(parameter);
        this.pathParam = pathParam;
    }

    @Override
    public Object apply(HttpServletRequest t, Map<String, String> pathParameters) {
        String result = pathParameters.get(pathParam.value());
        if (result == null) {
            throw new HttpRequestException(500, "Path parameter :" + pathParam.value() + " not matched");
        }
        return convertToParameterType(result, pathParam.value());
    }
}