package no.javabin.heroes;

import no.javabin.heroes.person.PersonDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PersonServiceTest {
  private PersonService service;
  private PersonDao dao;

  @Before
  public void setUp() throws Exception {
    dao = mock(PersonDao.class);
    service = new PersonService(dao);
  }

  @Test(expected = NotFoundException.class)
  public void shouldThrowExceptionWhenNotFound() throws Exception {
    when(dao.getPersonById(anyString())).thenReturn(Optional.empty());
    service.getPersonById("AnId");
  }
}