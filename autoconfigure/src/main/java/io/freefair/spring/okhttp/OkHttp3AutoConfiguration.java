package io.freefair.spring.okhttp;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp3AutoConfiguration extends OkHttpAutoConfiguration {

    @Autowired(required = false)
    private List<Configurer<OkHttpClient.Builder>> configurers;

    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @Autowired(required = false)
    @NetworkInterceptor
    private List<Interceptor> networkInterceptors;

    @Autowired(required = false)
    private CookieJar cookieJar;

    @Autowired(required = false)
    private Dns dns;

    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public Cache okHttp3Cache() throws IOException {
        File cacheDir = getCacheDir("okhttp3-cache");

        return new Cache(cacheDir, properties.getCache().getSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp3Client() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (properties.getCache().getMode() != OkHttpProperties.Cache.Mode.NONE) {
            builder.cache(okHttp3Cache());
        }

        builder.connectTimeout(properties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.readTimeout(properties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(properties.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.pingInterval(properties.getPingInterval().toMillis(), TimeUnit.MILLISECONDS);

        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }

        if (dns != null) {
            builder.dns(dns);
        }

        builder.followRedirects(properties.isFollowRedirects());
        builder.followSslRedirects(properties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(properties.isRetryOnConnectionFailure());

        if (applicationInterceptors != null && !applicationInterceptors.isEmpty()) {
            builder.interceptors().addAll(applicationInterceptors);
        }

        if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
            builder.networkInterceptors().addAll(networkInterceptors);
        }

        if (configurers != null) {
            for (Configurer<OkHttpClient.Builder> configurer : configurers) {
                configurer.configure(builder);
            }
        }

        return builder.build();
    }

    /**
     * @author Lars Grefer
     */
    @Configuration
    @ConditionalOnClass(OkHttp3ClientHttpRequestFactory.class)
    @AutoConfigureBefore(OkHttpRestTemplateAutoConfiguration.class)
    @AutoConfigureAfter(OkHttp3AutoConfiguration.class)
    public static class RequestFactoryAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(OkHttp3ClientHttpRequestFactory.class)
        public OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(OkHttpClient okHttpClient) {
            return new OkHttp3ClientHttpRequestFactory(okHttpClient);
        }
    }
}
