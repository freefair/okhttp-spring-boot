package io.freefair.spring.okhttp.logging;

import io.freefair.spring.okhttp.ApplicationInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
@ConditionalOnClass(HttpLoggingInterceptor.class)
@EnableConfigurationProperties(OkHttpLoggingInterceptorProperties.class)
public class OkHttp3LoggingInterceptorAutoConfiguration {

    @Autowired
    private OkHttpLoggingInterceptorProperties properties;

    @Autowired(required = false)
    private HttpLoggingInterceptor.Logger logger;

    @Bean
    @ApplicationInterceptor
    @ConditionalOnMissingBean
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor;

        if (logger != null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor(logger);
        } else {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
        }

        String level = properties.getLevel().toUpperCase(Locale.US);
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.valueOf(level));

        return httpLoggingInterceptor;
    }
}
