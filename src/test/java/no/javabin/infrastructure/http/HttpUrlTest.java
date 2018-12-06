package no.javabin.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HttpUrlTest {

    @Test
    public void shouldShowUrlWithoutParameters() {
        HttpUrl url = new HttpUrl("https://www.example.com");
        assertThat(url.toURL().toString()).isEqualTo("https://www.example.com");
    }

    @Test
    public void shouldAddParameters() {
        HttpUrl url = new HttpUrl("https://www.example.com").addParameter("foo", "bar");
        assertThat(url.toURL().toString()).isEqualTo("https://www.example.com?foo=bar");
    }

    @Test
    public void shouldUrlEncodeParameters() {
        HttpUrl url = new HttpUrl("https://www.example.com").addParameter("foo", "is + & = valid characters?");
        assertThat(url.toURL().toString()).isEqualTo("https://www.example.com?foo=is+%2B+%26+%3D+valid+characters%3F");
    }

}
