package io.freefair.spring.okhttp.autoconfigure;

import lombok.experimental.UtilityClass;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.util.Assert;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.List;

@UtilityClass
public class OkHttpSslUtil {

    public static void applySslBundle(OkHttpClient.Builder builder, SslBundle sslBundle) {
        Assert.notNull(builder, "builder must not be null");
        Assert.notNull(sslBundle, "sslBundle must not be null");

        ConnectionSpec connectionSpec = toConnectionSpec(sslBundle.getOptions());
        if (connectionSpec != null) {
            builder.connectionSpecs(List.of(connectionSpec));
        }

        SSLContext sslContext = sslBundle.createSslContext();
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();

        TrustManager[] trustManagers = sslBundle.getManagers().getTrustManagers();
        Assert.state(trustManagers.length == 1,
                "Trust material must be provided in the SSL bundle for OkHttp3ClientHttpRequestFactory");

        builder.sslSocketFactory(socketFactory, (X509TrustManager) trustManagers[0]);
    }

    @Nullable
    static ConnectionSpec toConnectionSpec(@Nullable SslOptions sslOptions) {
        if (sslOptions == null || !sslOptions.isSpecified()) {
            return null;
        }

        ConnectionSpec.Builder connectionSpecBuilder = new ConnectionSpec.Builder(true);

        if (sslOptions.getCiphers() != null) {
            connectionSpecBuilder.cipherSuites(sslOptions.getCiphers());
        }

        if (sslOptions.getEnabledProtocols() != null) {
            connectionSpecBuilder.tlsVersions(sslOptions.getEnabledProtocols());
        }

        return connectionSpecBuilder.build();
    }
}
