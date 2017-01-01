package io.freefair.spring.okhttp.logging;

import lombok.Getter;
import lombok.Setter;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lars Grefer
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "okhttp.logging")
public class OkHttp3LoggingInterceptorProperties {

    /**
     * The level at which the HttpLoggingInterceptor logs.
     */
    private HttpLoggingInterceptor.Level level;
}
