package io.freefair.spring.okhttp.logging;

import lombok.Getter;
import lombok.Setter;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@SuppressWarnings("WeakerAccess")
@Getter
@Setter
@ConfigurationProperties(prefix = "okhttp.logging")
public class OkHttpLoggingInterceptorProperties {

    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
}
