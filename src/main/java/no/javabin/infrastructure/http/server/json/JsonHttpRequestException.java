package no.javabin.infrastructure.http.server.json;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import no.javabin.infrastructure.http.server.HttpRequestException;
import org.jsonbuddy.JsonObject;

public class JsonHttpRequestException extends HttpRequestException {

    private JsonObject jsonObject;

    public JsonHttpRequestException(int errorCode, String message, JsonObject jsonObject) {
        super(errorCode, message);
        this.jsonObject = jsonObject;
    }

    @Override
    public void sendError(HttpServletResponse resp) throws IOException {
        resp.setStatus(getStatusCode());
        resp.setContentType("application/json");
        jsonObject.toJson(resp.getWriter());
    }

}
