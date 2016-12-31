package io.freefair.spring.okhttp;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Lars Grefer
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp3AutoConfiguration extends OkHttpAutoConfiguration {

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired(required = false)
    private List<OkHttp3Configurer> configurers;

    @SuppressWarnings({"SpringJavaAutowiringInspection", "MismatchedQueryAndUpdateOfCollection"})
    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @SuppressWarnings({"SpringJavaAutowiringInspection", "MismatchedQueryAndUpdateOfCollection"})
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
    public Cache okHttpCache() throws IOException {
        File cacheDir = getCacheDir("okhttp3-cache");

        return new Cache(cacheDir, properties.getCache().getSize());

    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (properties.getCache().getMode() != OkHttpProperties.Cache.Mode.NONE) {
            builder.cache(okHttpCache());
        }

        OkHttpProperties.Timeout connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.connectTimeout(connectTimeout.getValue(), connectTimeout.getUnit());
        }

        OkHttpProperties.Timeout readTimeout = properties.getReadTimeout();
        if (readTimeout != null) {
            builder.readTimeout(readTimeout.getValue(), readTimeout.getUnit());
        }

        OkHttpProperties.Timeout writeTimeout = properties.getWriteTimeout();
        if (writeTimeout != null) {
            builder.writeTimeout(writeTimeout.getValue(), writeTimeout.getUnit());
        }

        OkHttpProperties.PingInterval pingInterval = properties.getPingInterval();
        if(pingInterval != null) {
            builder.pingInterval(pingInterval.getValue(), pingInterval.getUnit());
        }

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
            for (OkHttp3Configurer configurer : configurers) {
                configurer.configure(builder);
            }
        }

        return builder.build();
    }
}
