package io.freefair.spring.okhttp;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Configuration
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttp3AutoConfiguration {

    @Autowired
    private OkHttpProperties properties;

    @Autowired(required = false)
    private List<OkHttp3Configurator> configurators;

    @Autowired(required = false)
    @ApplicationInterceptor
    private List<Interceptor> applicationInterceptors;

    @Autowired(required = false)
    @NetworkInterceptor
    private List<Interceptor> networkInterceptors;

    @Autowired(required = false)
    private CookieJar cookieJar;

    @Bean
    @ConditionalOnMissingBean
    public Cache okHttpCache() throws IOException {
        return new Cache(Files.createTempDirectory("okhttp-cache").toFile(), properties.getCache().getSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (properties.getCache().isEnabled()) {
            builder.cache(okHttpCache());
        }

        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
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

        if (applicationInterceptors != null && !applicationInterceptors.isEmpty()) {
            for (Interceptor interceptor : applicationInterceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        if (networkInterceptors != null && !networkInterceptors.isEmpty()) {
            for (Interceptor networkInterceptor : networkInterceptors) {
                builder.addNetworkInterceptor(networkInterceptor);
            }
        }

        if (configurators != null) {
            for (OkHttp3Configurator okHttpConfigurator : configurators) {
                okHttpConfigurator.configure(builder);
            }
        }

        return builder.build();
    }
}
