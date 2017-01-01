package io.freefair.spring.okhttp.logging;

import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import io.freefair.spring.okhttp.ApplicationInterceptor;
import io.freefair.spring.okhttp.OkHttp2AutoConfiguration;
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
@AutoConfigureBefore(OkHttp2AutoConfiguration.class)
@EnableConfigurationProperties(OkHttp2LoggingInterceptorProperties.class)
public class OkHttp2LoggingInterceptorAutoConfiguration {

    @Autowired
    private OkHttp2LoggingInterceptorProperties properties;

    @Autowired(required = false)
    private HttpLoggingInterceptor.Logger logger;

    @SuppressWarnings("Duplicates")
    @Bean
    @ApplicationInterceptor
    @ConditionalOnMissingBean
    public HttpLoggingInterceptor okHttp2LoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor;

        if (logger != null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor(logger);
        } else {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
        }

        HttpLoggingInterceptor.Level level = properties.getLevel();
        if (level != null) {
            httpLoggingInterceptor.setLevel(level);
        }

        return httpLoggingInterceptor;
    }
}
