package no.javabin.infrastructure.http.server;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import no.javabin.infrastructure.http.server.RequestParam.RequestParameterMapping;
import no.javabin.infrastructure.http.server.meta.AbstractHttpRequestParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpRequestParameterMapping;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@HttpParameterMapping(RequestParameterMapping.class)
public @interface RequestParam {

    String value();

    @Retention(RUNTIME)
    @Target(PARAMETER)
    @HttpParameterMapping(ClientIpParameterMapping.class)
    public @interface ClientIp {
    }


    public class RequestParameterMapping extends AbstractHttpRequestParameterMapping {
        private RequestParam reqParam;

        public RequestParameterMapping(RequestParam reqParam, Parameter parameter) {
            super(parameter);
            this.reqParam = reqParam;
        }

        @Override
        public Object apply(HttpServletRequest req, Map<String, String> pathParams) {
            return convertToParameterType(req.getParameter(reqParam.value()), reqParam.value());
        }
    }

    public class ClientIpParameterMapping implements HttpRequestParameterMapping {
        @Override
        public Object apply(HttpServletRequest req, Map<String, String> u) {
            return req.getRemoteAddr();
        }
    }

}
