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
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    public Optional<Boolean> admin;
    public int amount;

    private class ExampleController {

        @Get("/one")
        public JsonObject one(
                @RequestParam("name") Optional<String> name
        ) {
            return new JsonObject().put("name", name.orElse("Anonymous"));
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

        @Get("/hello")
        public void methodWithOptionalBoolean(@RequestParam("admin") Optional<Boolean> adminParam) {
            admin = adminParam;
        }

        @Get("/goodbye")
        public void methodWithRequiredInt(@RequestParam("amount") int amountParam) {
            amount = amountParam;
        }

        @Post("/setLoggedInUser")
        public void sessionUpdater(@SessionParameter("username") Consumer<String> usernameSetter) {
            usernameSetter.accept("Alice Bobson");
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

    @Test
    public void shouldCallWithOptionalParameter() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/hello");

        assertThat(admin).isNull();
        servlet.doGet(requestMock, responseMock);
        assertThat(admin).isEmpty();

        when(requestMock.getParameter("admin")).thenReturn("true");
        servlet.doGet(requestMock, responseMock);
        assertThat(admin).hasValue(true);
    }


    @Test
    public void shouldCallWithRequiredInt() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/goodbye");

        when(requestMock.getParameter("amount")).thenReturn("123");
        servlet.doGet(requestMock, responseMock);
        assertThat(amount).isEqualTo(123);
    }

    @Test
    public void shouldRequireNonOptionalParameter() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/goodbye");

        servlet.doGet(requestMock, responseMock);
        verify(responseMock).sendError(400, "Missing required parameter amount");
    }

    @Test
    public void shouldReportParameterConversionFailure() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/goodbye");

        when(requestMock.getParameter("amount")).thenReturn("one");
        servlet.doGet(requestMock, responseMock);
        verify(responseMock).sendError(400, "Invalid parameter amount 'one' is not an int");
    }

    @Test
    public void shouldSetSessionParameters() throws ServletException, IOException {
        when(requestMock.getPathInfo()).thenReturn("/setLoggedInUser");

        HttpSession mockSession = Mockito.mock(HttpSession.class);
        when(requestMock.getSession(Mockito.anyBoolean())).thenReturn(mockSession);

        servlet.doPost(requestMock, responseMock);
        verify(mockSession).setAttribute("username", "Alice Bobson");

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
