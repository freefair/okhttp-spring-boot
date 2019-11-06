package io.freefair.spring.okhttp;

import okhttp3.*;
import org.springframework.beans.factory.ObjectProvider;
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
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp3AutoConfiguration {

    @Autowired
    private OkHttpProperties okHttpProperties;

    @Autowired
    private ObjectProvider<Configurer<OkHttpClient.Builder>> configurers;

    @Autowired
    @ApplicationInterceptor
    private ObjectProvider<Interceptor> applicationInterceptors;

    @Autowired
    @NetworkInterceptor
    private ObjectProvider<Interceptor> networkInterceptors;

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp3Client(
            ObjectProvider<Cache> cache,
            ObjectProvider<CookieJar> cookieJar,
            ObjectProvider<Dns> dns,
            ConnectionPool connectionPool
    ) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        cache.ifUnique(builder::cache);

        builder.connectTimeout(okHttpProperties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.readTimeout(okHttpProperties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.writeTimeout(okHttpProperties.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS);
        builder.pingInterval(okHttpProperties.getPingInterval().toMillis(), TimeUnit.MILLISECONDS);

        cookieJar.ifUnique(builder::cookieJar);

        dns.ifUnique(builder::dns);

        builder.connectionPool(connectionPool);

        builder.followRedirects(okHttpProperties.isFollowRedirects());
        builder.followSslRedirects(okHttpProperties.isFollowSslRedirects());
        builder.retryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure());

        applicationInterceptors.forEach(i -> builder.interceptors().add(i));

        networkInterceptors.forEach(i -> builder.networkInterceptors().add(i));

        configurers.forEach(configurer -> configurer.configure(builder));

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
    public Cache okHttp3Cache() throws IOException {
        File directory = okHttpProperties.getCache().getDirectory();
        if (directory == null) {
            directory = Files.createTempDirectory("okhttp-cache").toFile();
        }
        return new Cache(directory, okHttpProperties.getCache().getMaxSize().toBytes());
    }
}
