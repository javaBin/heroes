package no.javabin.heroes;

public class HttpRequestException extends RuntimeException {

    private int statusCode;

    public HttpRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpRequestException(int statusCode, Throwable e) {
        super(e);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
