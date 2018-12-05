package no.javabin.infrastructure.http.server;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SessionParameter {

    String value();

}
