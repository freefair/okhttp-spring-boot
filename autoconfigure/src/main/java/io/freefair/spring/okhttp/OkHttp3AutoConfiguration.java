package io.freefair.spring.okhttp;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
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
            @Autowired Optional<Cache> cache,
            @Autowired Optional<CookieJar> cookieJar,
            @Autowired Optional<Dns> dns,
            ConnectionPool connectionPool
    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        cache.ifPresent(builder::cache);

        builder.connectTimeout(okHttpProperties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.readTimeout(okHttpProperties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(okHttpProperties.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.pingInterval(okHttpProperties.getPingInterval().toMillis(), TimeUnit.MILLISECONDS);

        cookieJar.ifPresent(builder::cookieJar);

        dns.ifPresent(builder::dns);

        builder.connectionPool(connectionPool);

        builder.followRedirects(okHttpProperties.isFollowRedirects());
        builder.followSslRedirects(okHttpProperties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure());

        if (applicationInterceptors != null) {
            builder.interceptors().addAll(applicationInterceptors);
        }

        if (networkInterceptors != null) {
            builder.networkInterceptors().addAll(networkInterceptors);
        }

        if (configurers != null) {
            configurers.forEach(configurer -> configurer.configure(builder));
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionPool okHttp3ConnectionPool() {
        int maxIdleConnections = okHttpProperties.getConnectionPool().getMaxIdleConnections();
        Duration keepAliveDuration = okHttpProperties.getConnectionPool().getKeepAliveDuration();
        return new ConnectionPool(maxIdleConnections, keepAliveDuration.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "okhttp.cache.enabled", havingValue = "true", matchIfMissing = true)
    public Cache okhttp3Cache() throws IOException {
        File directory = okHttpProperties.getCache().getDirectory();
        if (directory == null) {
            directory = Files.createTempDirectory("okhttp-cache").toFile();
        }
        return new Cache(directory, okHttpProperties.getCache().getMaxSize().toBytes());
    }
}
