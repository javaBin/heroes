package no.javabin.infrastructure.http.server.meta;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface HttpResponseValueMapping {

    void accept(Object result, HttpServletResponse resp) throws IOException;


}
