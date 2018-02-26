package io.freefair.spring.okhttp;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Dns;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
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
public class OkHttp2AutoConfiguration extends OkHttpAutoConfiguration {

    @Autowired(required = false)
    private List<Configurer<OkHttpClient>> configurers;

    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @Autowired(required = false)
    @NetworkInterceptor
    private List<Interceptor> networkInterceptors;

    @Autowired(required = false)
    private CookieHandler cookieHandler;

    @Autowired(required = false)
    private Dns dns;

    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public Cache okHttp2Cache() throws IOException {
        File cacheDir = getCacheDir("okhttp2-cache");

        return new Cache(cacheDir, properties.getCache().getSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp2Client() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        if (properties.getCache().getMode() != OkHttpProperties.Cache.Mode.NONE) {
            okHttpClient.setCache(okHttp2Cache());
        }

        if (cookieHandler != null) {
            okHttpClient.setCookieHandler(cookieHandler);
        }

        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            okHttpClient.setConnectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        Duration readTimeout = properties.getReadTimeout();
        if (readTimeout != null) {
            okHttpClient.setReadTimeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        Duration writeTimeout = properties.getWriteTimeout();
        if (writeTimeout != null) {
            okHttpClient.setWriteTimeout(writeTimeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        if (dns != null) {
            okHttpClient.setDns(dns);
        }

        okHttpClient.setFollowRedirects(properties.isFollowRedirects());
        okHttpClient.setFollowSslRedirects(properties.isFollowSslRedirects());
        okHttpClient.setRetryOnConnectionFailure(properties.isRetryOnConnectionFailure());

        if (applicationInterceptors != null && !applicationInterceptors.isEmpty()) {
            okHttpClient.interceptors().addAll(applicationInterceptors);
        }

        if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
            okHttpClient.networkInterceptors().addAll(networkInterceptors);
        }

        if (configurers != null) {
            for (Configurer<OkHttpClient> configurer : configurers) {
                configurer.configure(okHttpClient);
            }
        }

        return okHttpClient;
    }
}
