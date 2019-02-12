package no.javabin.infrastructure.http.server;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import no.javabin.infrastructure.http.server.meta.HttpParameterMapping;
import no.javabin.infrastructure.http.server.meta.HttpRequestParameterMapping;
import org.junit.Test;

public class ApiServletConfigurationErrorTest {

    @Test
    public void shouldReportAllActionErrors() throws ServletException {
        ApiServlet apiServlet = new ApiServlet() {
            @Override
            public void init() throws ServletException {
                registerController(new ControllerWithErrors());
                registerController(new OtherControllerWithErrors());
            }
        };

        assertThatThrownBy(() -> apiServlet.init(null))
            .isInstanceOf(ApiServletException.class)
            .hasMessageContaining(ControllerWithErrors.class.getName())
            .hasMessageContaining("actionWithUnboundParameter")
            .hasMessageContaining("actionWithUnknownReturnType")
            .hasMessageContaining(OtherControllerWithErrors.class.getName())
            .hasMessageContaining("actionWithInvalidMappingAnnotation")
            ;
    }


    public static class ParameterMappingWithoutProperConstructor implements HttpRequestParameterMapping {

        public ParameterMappingWithoutProperConstructor(@SuppressWarnings("unused") String string) {
        }

        @Override
        public Object apply(HttpServletRequest req, Map<String, String> pathParameters) throws IOException {
            return null;
        }

    }


    @Retention(RUNTIME)
    @HttpParameterMapping(ParameterMappingWithoutProperConstructor.class)
    public @interface CustomAnnotation {

    }


    private class ControllerWithErrors {

        @Get("/")
        public void actionWithUnboundParameter(@SuppressWarnings("unused") String parameter) {

        }

        @Post("/")
        public Object actionWithUnknownReturnType() {
            return null;
        }

    }


    private class OtherControllerWithErrors {

        @Get("/foo")
        public String actionWithInvalidMappingAnnotation(
                @CustomAnnotation String parameter
        ) {
            return parameter;
        }

    }



}
