package no.javabin.infrastructure.http.server;

public class ApiServletException extends RuntimeException {

    public ApiServletException(String message) {
        super(message);
    }

}
