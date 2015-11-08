package no.javabin.heroes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    @After
    public void tearDown() throws Exception {
        serviceLocator.close();

    }
}