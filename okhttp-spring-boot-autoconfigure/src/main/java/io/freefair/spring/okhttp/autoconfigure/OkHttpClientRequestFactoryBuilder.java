package io.freefair.spring.okhttp.autoconfigure;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.util.Assert;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
    public OkHttpClientRequestFactory build(@Nullable ClientHttpRequestFactorySettings settings) {
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
            Assert.state(!sslBundle.getOptions().isSpecified(), "SSL Options cannot be specified with OkHttp");

            SSLContext sslContext = sslBundle.createSslContext();
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            TrustManager[] trustManagers = sslBundle.getManagers().getTrustManagers();
            Assert.state(trustManagers.length == 1,
                    "Trust material must be provided in the SSL bundle for OkHttp3ClientHttpRequestFactory");

            builder.sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0]);
        }

        ClientHttpRequestFactorySettings.Redirects redirects = settings.redirects();
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
