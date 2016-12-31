package io.freefair.spring.okhttp.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Lars Grefer
 */
@SuppressWarnings("WeakerAccess")
@Getter
@Setter
@ConfigurationProperties(prefix = "okhttp.logging")
public class OkHttpLoggingInterceptorProperties {

    private String level;
}
