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
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp3AutoConfiguration {

    @Autowired
    private OkHttpProperties okHttpProperties;

    @Autowired(required = false)
    private List<Configurer<OkHttpClient.Builder>> configurers;

    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @Autowired(required = false)
    @NetworkInterceptor
    private List<Interceptor> networkInterceptors;

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp3Client(
            @Autowired(required = false) Cache cache,
            @Autowired(required = false) CookieJar cookieJar,
            @Autowired(required = false) Dns dns
    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (cache == null) {
            cache = createCache(okHttpProperties.getCache());
        }
        if (cache != null) {
            builder.cache(cache);
        }

        builder.connectTimeout(okHttpProperties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.readTimeout(okHttpProperties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(okHttpProperties.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.pingInterval(okHttpProperties.getPingInterval().toMillis(), TimeUnit.MILLISECONDS);

        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }

        if (dns != null) {
            builder.dns(dns);
        }

        builder.followRedirects(okHttpProperties.isFollowRedirects());
        builder.followSslRedirects(okHttpProperties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure());

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

    private Cache createCache(OkHttpProperties.Cache cacheProperties) {
        if (cacheProperties.getDirectory() != null && cacheProperties.getMaxSize() > 0) {
            return new Cache(cacheProperties.getDirectory(), cacheProperties.getMaxSize());
        } else {
            return null;
        }
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
