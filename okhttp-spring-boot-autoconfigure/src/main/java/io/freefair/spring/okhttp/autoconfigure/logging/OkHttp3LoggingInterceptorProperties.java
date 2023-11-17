package io.freefair.spring.okhttp.autoconfigure.logging;

import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lars Grefer
 */
@Data
@ConfigurationProperties(prefix = "okhttp.logging")
public class OkHttp3LoggingInterceptorProperties {

    /**
     * The level at which the HttpLoggingInterceptor logs.
     */
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
}
