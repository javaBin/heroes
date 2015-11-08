package no.javabin.heroes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataServletTest {

    private ServiceLocator serviceLocator;
    private PersonService personService;

    @Before
    public void setUp() throws Exception {
        personService = mock(PersonService.class);
        serviceLocator = ServiceLocator.startThreadContext();
        serviceLocator.setPersonService(personService);
    }

    @Test
    public void shouldFindPerson() throws Exception {
        DataServlet dataServlet = new DataServlet();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getPathInfo()).thenReturn("/person/mypersonid");
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));


        dataServlet.doGet(req,resp);

        verify(personService).getPersonById("mypersonid");

    }

    @After
    public void tearDown() throws Exception {
        serviceLocator.close();

    }
}