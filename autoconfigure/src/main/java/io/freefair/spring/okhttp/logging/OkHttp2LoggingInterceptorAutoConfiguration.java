package io.freefair.spring.okhttp.logging;

import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import io.freefair.spring.okhttp.ApplicationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableConfigurationProperties(OkHttpLoggingInterceptorProperties.class)
public class OkHttp2LoggingInterceptorAutoConfiguration {

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

        String level = properties.getLevel();
        if (level != null && !level.isEmpty()) {
            HttpLoggingInterceptor.Level logLevel = EnumHelper.valueOf(HttpLoggingInterceptor.Level.class, level);
            httpLoggingInterceptor.setLevel(logLevel);
        }

        return httpLoggingInterceptor;
    }
}
