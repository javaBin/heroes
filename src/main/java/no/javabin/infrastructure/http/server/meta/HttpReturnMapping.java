package no.javabin.infrastructure.http.server.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HttpReturnMapping {

    Class<? extends HttpResponseValueMapping> value();

}
