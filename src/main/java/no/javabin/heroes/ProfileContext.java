package no.javabin.heroes;

import java.io.IOException;

import no.javabin.infrastructure.http.HttpUrl;

public interface ProfileContext {

    HttpUrl createAuthorizationUrl(String state, boolean admin);

    Profile exchangeCodeForProfile(String code) throws IOException;

}
