package io.freefair.spring.okhttp.autoconfigure.logging;

import io.freefair.spring.okhttp.ApplicationInterceptor;
import io.freefair.spring.okhttp.autoconfigure.OkHttp3AutoConfiguration;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Lars Grefer
 */
@AutoConfiguration(before = OkHttp3AutoConfiguration.class)
@ConditionalOnClass(HttpLoggingInterceptor.class)
@EnableConfigurationProperties(OkHttp3LoggingInterceptorProperties.class)
public class OkHttp3LoggingInterceptorAutoConfiguration {

    @Bean
    @ApplicationInterceptor
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "okhttp.logging.enabled", havingValue = "true", matchIfMissing = true)
    public HttpLoggingInterceptor okHttp3LoggingInterceptor(
            OkHttp3LoggingInterceptorProperties properties,
            ObjectProvider<HttpLoggingInterceptor.Logger> logger
    ) {
        HttpLoggingInterceptor.Logger actualLogger = logger.getIfUnique(() -> HttpLoggingInterceptor.Logger.DEFAULT);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(actualLogger);

        httpLoggingInterceptor.setLevel(properties.getLevel());

        return httpLoggingInterceptor;
    }
}
