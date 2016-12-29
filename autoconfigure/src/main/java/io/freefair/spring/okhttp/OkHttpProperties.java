package io.freefair.spring.okhttp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@ConfigurationProperties(prefix = "okhttp")
public class OkHttpProperties {

    private Timeout connectTimeout;

    private Timeout readTimeout;

    private Timeout writeTimeout;

    @Setter(NONE)
    private Cache cache = new Cache();

    @Getter
    @Setter
    public static class Timeout {

        private long value;

        private TimeUnit unit;
    }

    @Getter
    @Setter
    public static class Cache {
        private long size = 10 * 1024 * 1024;

        private boolean enabled = true;
    }
}
