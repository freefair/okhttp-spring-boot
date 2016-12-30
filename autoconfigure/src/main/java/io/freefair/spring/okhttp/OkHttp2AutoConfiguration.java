package io.freefair.spring.okhttp;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.CookieHandler;
import java.nio.file.Files;
import java.util.List;

@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp2AutoConfiguration {

    @Autowired
    private OkHttpProperties properties;

    @Autowired(required = false)
    private List<OkHttp2Configurator> configurators;

    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @Autowired(required = false)
    @NetworkInterceptor
    private List<Interceptor> networkInterceptors;

    @Autowired(required = false)
    private CookieHandler cookieHandler;

    @Bean
    @ConditionalOnMissingBean
    public Cache okHttpCache() throws IOException {
        return new Cache(Files.createTempDirectory("okhttp-cache").toFile(), properties.getCache().getSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();

        if (properties.getCache().isEnabled()) {
            okHttpClient.setCache(okHttpCache());
        }

        if (cookieHandler != null) {
            okHttpClient.setCookieHandler(cookieHandler);
        }

        OkHttpProperties.Timeout connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            okHttpClient.setConnectTimeout(connectTimeout.getValue(), connectTimeout.getUnit());
        }

        OkHttpProperties.Timeout readTimeout = properties.getReadTimeout();
        if (readTimeout != null) {
            okHttpClient.setReadTimeout(readTimeout.getValue(), readTimeout.getUnit());
        }

        OkHttpProperties.Timeout writeTimeout = properties.getWriteTimeout();
        if (writeTimeout != null) {
            okHttpClient.setWriteTimeout(writeTimeout.getValue(), writeTimeout.getUnit());
        }

        if (applicationInterceptors != null && !applicationInterceptors.isEmpty()) {
            okHttpClient.interceptors().addAll(applicationInterceptors);
        }

        if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
            okHttpClient.networkInterceptors().addAll(networkInterceptors);
        }

        if (configurators != null) {
            for (OkHttp2Configurator okHttpConfigurator : configurators) {
                okHttpConfigurator.configure(okHttpClient);
            }
        }

        return okHttpClient;
    }
}
