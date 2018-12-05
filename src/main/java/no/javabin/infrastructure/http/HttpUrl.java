package no.javabin.infrastructure.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import no.javabin.infrastructure.ExceptionUtil;

public class HttpUrl {

    private String baseUrl;
    private Map<String, String> parameters = new LinkedHashMap<>();

    public HttpUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HttpUrl addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public URL toURL() {
        try {
            return new URL(baseUrl + (parameters.isEmpty() ? "" : "?" + urlEncode(parameters)));
        } catch (MalformedURLException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    private String urlEncode(Map<String, String> parameters) {
        StringBuilder result = new StringBuilder();
        for (Entry<String, String> entry : parameters.entrySet()) {
            if (result.length() > 0) result.append("&");

            result.append(urlEncode(entry.getKey()))
                    .append("=")
                    .append(urlEncode(entry.getValue()));
        }
        return result.toString();
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should always be supported", e);
        }
    }

}
