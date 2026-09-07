package io.freefair.spring.okhttp.autoconfigure;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.HttpRedirects;
import org.springframework.boot.ssl.SslBundle;

import java.time.Duration;

/**
 * @author Lars Grefer
 */
@RequiredArgsConstructor
public class OkHttpClientRequestFactoryBuilder implements ClientHttpRequestFactoryBuilder<OkHttpClientRequestFactory> {

    private final OkHttpClient okHttpClient;

    @Override
    public OkHttpClientRequestFactory build() {
        return this.build(null);
    }

    @Override
    public OkHttpClientRequestFactory build(@Nullable HttpClientSettings settings) {
        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        if (settings == null) {
            return new OkHttpClientRequestFactory(builder.build());
        }

        Duration connectTimeout = settings.connectTimeout();
        if (connectTimeout != null) {
            builder.connectTimeout(connectTimeout);
        }

        Duration readTimeout = settings.readTimeout();
        if (readTimeout != null) {
            builder.readTimeout(readTimeout);
        }

        SslBundle sslBundle = settings.sslBundle();
        if (sslBundle != null) {
            OkHttpSslUtil.applySslBundle(builder, sslBundle);
        }

        HttpRedirects redirects = settings.redirects();
        if (redirects != null) {
            switch (redirects) {
                case FOLLOW_WHEN_POSSIBLE, FOLLOW -> {
                    builder.followRedirects(true);
                    builder.followSslRedirects(true);
                }
                case DONT_FOLLOW -> {
                    builder.followRedirects(false);
                    builder.followSslRedirects(true);
                }
                default -> {}
            }
        }

        return new OkHttpClientRequestFactory(builder.build());
    }


}
