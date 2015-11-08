package no.javabin.heroes;

import no.javabin.heroes.person.PersonService;
import org.jsonbuddy.JsonFactory;
import org.jsonbuddy.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataServletTest {

    private ServiceLocator serviceLocator;
    private PersonService personService;
    private DataServlet dataServlet;
    private HttpServletRequest req;
    private HttpServletResponse resp;

    @Before
    public void setUp() throws Exception {
        personService = mock(PersonService.class);
        serviceLocator = ServiceLocator.startThreadContext();
        serviceLocator.setPersonService(personService);
        dataServlet = new DataServlet();
        req = mock(HttpServletRequest.class);
        resp = mock(HttpServletResponse.class);
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    public void shouldFindPerson() throws Exception {
        when(req.getPathInfo()).thenReturn("/person/mypersonid");

        dataServlet.doGet(req, resp);

        verify(personService).getPersonById("mypersonid");
    }

    @Test
    public void shouldFindAllPeople() throws Exception {
        when(req.getPathInfo()).thenReturn("/person");

        dataServlet.doGet(req, resp);

        verify(personService).getAllPersons();
    }

    @Test
    public void shouldAddPerson() throws Exception {
        when(req.getPathInfo()).thenReturn("/person");

        JsonObject jsonObject = JsonFactory.jsonObject().put("name", "Pete");
        ServletInputStream servletInput = makeMock(jsonObject.toJson());
        when(req.getInputStream()).thenReturn(servletInput);

        dataServlet.doPost(req,resp);

        verify(personService).insertPerson(jsonObject);

    }

    private ServletInputStream makeMock(String s) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        return new ServletInputStream() {
            private boolean isFinished = false;
            @Override
            public boolean isFinished() {
               return isFinished;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                int read = byteArrayInputStream.read();
                isFinished = (read == -1);
                return read;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        serviceLocator.close();

    }
}