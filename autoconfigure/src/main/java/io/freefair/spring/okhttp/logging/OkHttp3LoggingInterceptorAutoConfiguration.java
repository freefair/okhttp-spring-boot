package io.freefair.spring.okhttp.logging;

import io.freefair.spring.okhttp.ApplicationInterceptor;
import io.freefair.spring.okhttp.OkHttp3AutoConfiguration;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.ObjectProvider;
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
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(HttpLoggingInterceptor.class)
@AutoConfigureBefore(OkHttp3AutoConfiguration.class)
@EnableConfigurationProperties(OkHttp3LoggingInterceptorProperties.class)
public class OkHttp3LoggingInterceptorAutoConfiguration {

    @Autowired
    private OkHttp3LoggingInterceptorProperties properties;

    @Bean
    @ApplicationInterceptor
    @ConditionalOnMissingBean
    public HttpLoggingInterceptor okHttp3LoggingInterceptor(
            ObjectProvider<HttpLoggingInterceptor.Logger> logger
    ) {
        HttpLoggingInterceptor httpLoggingInterceptor;

        if (logger.getIfAvailable() != null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor(logger.getIfAvailable());
        }
        else {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
        }

        //noinspection deprecation
        httpLoggingInterceptor.setLevel(properties.getLevel());

        return httpLoggingInterceptor;
    }
}
