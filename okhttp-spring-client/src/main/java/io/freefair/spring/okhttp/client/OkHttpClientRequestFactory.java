package io.freefair.spring.okhttp.client;

import okhttp3.OkHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.lang.NonNull;

import java.net.URI;

/**
 * OkHttp based {@link ClientHttpRequestFactory} implementation.
 * <p>
 * Serves as replacement for the deprecated {@link org.springframework.http.client.OkHttp3ClientHttpRequestFactory}.
 *
 * @author Lars Grefer
 */
public record OkHttpClientRequestFactory(@NonNull OkHttpClient okHttpClient) implements ClientHttpRequestFactory {

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
        return new OkHttpClientRequest(okHttpClient, uri, httpMethod);
    }

}
