package io.freefair.spring.okhttp.logging;

import io.freefair.spring.okhttp.ApplicationInterceptor;
import io.freefair.spring.okhttp.OkHttp3AutoConfiguration;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lars Grefer
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HttpLoggingInterceptor.class)
@AutoConfigureBefore(OkHttp3AutoConfiguration.class)
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

        //noinspection deprecation
        httpLoggingInterceptor.setLevel(properties.getLevel());

        return httpLoggingInterceptor;
    }
}
