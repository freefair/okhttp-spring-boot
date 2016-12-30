package io.freefair.spring.okhttp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.NONE;

@SuppressWarnings("WeakerAccess")
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

        private long value = 10_000;

        private TimeUnit unit = TimeUnit.MILLISECONDS;
    }

    @Getter
    @Setter
    public static class Cache {
        /**
         * The maximum number of bytes this cache should use to store
         */
        private long size = 10485760;

        private boolean enabled = true;
    }
}
