package io.freefair.spring.okhttp.logging;

import io.freefair.spring.okhttp.ApplicationInterceptor;
import io.freefair.spring.okhttp.OkHttp3AutoConfiguration;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lars Grefer
 */
@Configuration
@ConditionalOnClass(HttpLoggingInterceptor.class)
@AutoConfigureBefore(OkHttp3AutoConfiguration.class)
@EnableConfigurationProperties(OkHttp3LoggingInterceptorProperties.class)
public class OkHttp3LoggingInterceptorAutoConfiguration {

    @Autowired
    private OkHttp3LoggingInterceptorProperties properties;

    @SuppressWarnings("Duplicates")
    @Bean
    @ApplicationInterceptor
    @ConditionalOnMissingBean
    public HttpLoggingInterceptor okHttp3LoggingInterceptor(
            @Autowired(required = false) HttpLoggingInterceptor.Logger logger
    ) {
        HttpLoggingInterceptor httpLoggingInterceptor;

        if (logger != null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor(logger);
        } else {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
        }

        httpLoggingInterceptor.setLevel(properties.getLevel());

        return httpLoggingInterceptor;
    }
}
