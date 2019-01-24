package no.javabin.infrastructure.http.server;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletResponse;

import no.javabin.infrastructure.http.server.meta.HttpResponseValueMapping;
import no.javabin.infrastructure.http.server.meta.HttpReturnMapping;

@Retention(RUNTIME)
@Target(METHOD)
@HttpReturnMapping(SendRedirectMapping.class)
public @interface SendRedirect {

}

class SendRedirectMapping implements HttpResponseValueMapping {
    @Override
    public void accept(Object result, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(result.toString());
    }
}