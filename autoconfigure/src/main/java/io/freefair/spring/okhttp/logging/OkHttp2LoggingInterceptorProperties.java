package io.freefair.spring.okhttp.logging;

import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lars Grefer
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "okhttp.logging")
public class OkHttp2LoggingInterceptorProperties {

    /**
     * The level at which the HttpLoggingInterceptor logs.
     */
    private HttpLoggingInterceptor.Level level;
}
