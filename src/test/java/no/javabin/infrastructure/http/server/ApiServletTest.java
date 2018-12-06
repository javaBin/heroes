package no.javabin.infrastructure.http.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.javabin.heroes.HttpRequestException;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ApiServletTest {

    private ApiServlet servlet = new ApiServlet();
    private HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
    private HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);
    private StringWriter responseBody = new StringWriter();
    public JsonObject postedBody;

    private class ExampleController {

        @Get("/one")
        public JsonObject one(
                @RequestParam("name") String name
        ) {
            return new JsonObject().put("name", name);
        }

        @Get("/error")
        public void throwError() {
            throw new HttpRequestException(401, "You are not authorized");
        }

        // TODO: Avoid using undeclared path parameters
        @Get("/user/:userId/message/:messageId")
        public URL privateMethod(
                @PathParam("userId") String userId,
                @PathParam("messageId") String messageId
        ) throws MalformedURLException {
            return new URL("https://messages.example.com/?user=" + userId + "&message=" + messageId);
        }

        @Post("/postMethod")
        public void postAction(@Body JsonObject o) {
            postedBody = o;
        }
    }

    @Test
    public void shouldCallMethodWithArgumentsAndConvertReturn() throws ServletException, IOException {
        String name = UUID.randomUUID().toString();
        when(requestMock.getPathInfo()).thenReturn("/one");
        when(requestMock.getParameter("name")).thenReturn(name);

        servlet.doGet(requestMock, responseMock);

        assertThat(JsonParser.parseToObject(responseBody.toString()).requiredString("name"))
            .isEqualTo(name);
        verify(responseMock).getWriter();
        verify(responseMock).setContentType("application/json");
    }

    @Test
    public void shouldOutputErrorToResponse() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/error");

        servlet.doGet(requestMock, responseMock);
        verify(responseMock).sendError(401, "You are not authorized");
    }

    @Test
    public void shouldGive404OnUnknownAction() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/missing");
        servlet.doGet(requestMock, responseMock);
        verify(responseMock).sendError(404);
    }

    @Test
    public void shouldDecodePathParams() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/user/3341/message/abc");
        servlet.doGet(requestMock, responseMock);
        verify(responseMock).sendRedirect("https://messages.example.com/?user=3341&message=abc");
    }

    @Test
    public void shouldPostJson() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/postMethod");

        JsonObject requestObject = new JsonObject()
                .put("foo", "bar")
                .put("list", Arrays.asList("a", "b", "c"));
        when(requestMock.getReader())
            .thenReturn(new BufferedReader(new StringReader(requestObject.toIndentedJson(" "))));
        servlet.doPost(requestMock, responseMock);

        assertThat(postedBody).isEqualTo(requestObject);
    }

    @Before
    public void setupRequest() throws IOException {
        servlet.registerController(new ExampleController());

        when(responseMock.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @After
    public void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(responseMock);
    }
}
