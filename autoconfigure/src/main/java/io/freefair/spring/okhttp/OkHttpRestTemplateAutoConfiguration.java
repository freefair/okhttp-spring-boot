package io.freefair.spring.okhttp;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.time.Duration;
import java.util.function.Function;

/**
 * @author Lars Grefer
 * @see RestTemplateAutoConfiguration
 */
@AutoConfiguration(before = RestTemplateAutoConfiguration.class, after = HttpMessageConvertersAutoConfiguration.class)
@ConditionalOnClass({RestTemplateCustomizer.class, RestTemplate.class})
@Conditional(NotReactiveWebApplicationCondition.class)
public class OkHttpRestTemplateAutoConfiguration {

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplateBuilderConfigurer,
                                                   OkHttpClient okHttpClient) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder = builder.requestFactory(new RequestFactoryFunction(okHttpClient));
        return restTemplateBuilderConfigurer.configure(builder);
    }

    @AllArgsConstructor
    static class RequestFactoryFunction implements Function<ClientHttpRequestFactorySettings, ClientHttpRequestFactory> {

        private OkHttpClient okHttpClient;

        @Override
        public ClientHttpRequestFactory apply(ClientHttpRequestFactorySettings settings) {

            OkHttpClient.Builder builder = okHttpClient.newBuilder();

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

            return new OkHttpClientRequestFactory(builder.build());
        }
    }

}
