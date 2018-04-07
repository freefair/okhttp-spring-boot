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

import java.net.CookieHandler;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lars Grefer
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp2AutoConfiguration {

    @Autowired
    private OkHttpProperties okHttpProperties;

    @Autowired(required = false)
    private List<Configurer<OkHttpClient>> configurers;

    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @Autowired(required = false)
    @NetworkInterceptor
    private List<Interceptor> networkInterceptors;

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttp2Client(
            @Autowired(required = false) Cache cache,
            @Autowired(required = false) CookieHandler cookieHandler,
            @Autowired(required = false) Dns dns
    ) {
        OkHttpClient okHttpClient = new OkHttpClient();

        if (cache == null) {
            cache = createCache(okHttpProperties.getCache());
        }
        if (cache != null) {
            okHttpClient.setCache(cache);
        }

        okHttpClient.setCookieHandler(cookieHandler);

        okHttpClient.setConnectTimeout(okHttpProperties.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(okHttpProperties.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(okHttpProperties.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS);

        okHttpClient.setDns(dns);

        okHttpClient.setFollowRedirects(okHttpProperties.isFollowRedirects());
        okHttpClient.setFollowSslRedirects(okHttpProperties.isFollowSslRedirects());
        okHttpClient.setRetryOnConnectionFailure(okHttpProperties.isRetryOnConnectionFailure());

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

    private Cache createCache(OkHttpProperties.Cache cacheProperties) {
        if (cacheProperties.getDirectory() != null && cacheProperties.getMaxSize() < 0) {
            return new Cache(cacheProperties.getDirectory(), cacheProperties.getMaxSize());
        } else {
            return null;
        }
    }
}
