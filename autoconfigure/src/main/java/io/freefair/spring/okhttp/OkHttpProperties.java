package io.freefair.spring.okhttp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.NONE;

/**
 * @author Lars Grefer
 */
@SuppressWarnings("WeakerAccess")
@Getter
@Setter
@ConfigurationProperties(prefix = "okhttp")
public class OkHttpProperties {

    /**
     * The default connect timeout for new connections.
     */
    private Timeout connectTimeout;

    /**
     * The default read timeout for new connections.
     */
    private Timeout readTimeout;

    /**
     * The default write timeout for new connections.
     */
    private Timeout writeTimeout;

    /**
     * The interval between web socket pings initiated by this client. Use this to
     * automatically send web socket ping frames until either the web socket fails or it is closed.
     * This keeps the connection alive and may detect connectivity failures early. No timeouts are
     * enforced on the acknowledging pongs.
     * <p>
     * <p>The default value of 0 disables client-initiated pings.
     */
    private PingInterval pingInterval;

    @Setter(NONE)
    private Cache cache = new Cache();

    /**
     * Whether to follow redirects from HTTPS to HTTP and from HTTP to HTTPS.
     */
    private boolean followSslRedirects = true;

    /**
     * Whether to follow redirects.
     */
    private boolean followRedirects = true;


    /**
     * Whether to retry or not when a connectivity problem is encountered.
     */
    private boolean retryOnConnectionFailure = true;

    @Getter
    @Setter
    public static class Duration {
        private TimeUnit unit = TimeUnit.MILLISECONDS;
    }

    @Getter
    @Setter
    public static class Timeout extends Duration {

        /**
         * A value of 0 means no timeout, otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        private long value = 10_000;
    }

    @Getter
    @Setter
    public static class PingInterval extends Duration {

        /**
         * The interval between web socket pings initiated by this client (The default value of 0 disables client-initiated pings).
         */
        private long value = 0;
    }

    @Getter
    @Setter
    public static class Cache {
        /**
         * The maximum number of bytes this cache should use to store.
         */
        private long size = 10485760;

        /**
         * The path of the directory where the cache should be stored.
         */
        private String directory;

        private Mode mode = Mode.TEMPORARY;

        public enum Mode {
            /**
             * No caching.
             */
            NONE,
            /**
             * Caching in a temporary directory.
             */
            TEMPORARY,
            /**
             * Caching in a persistent directory.
             */
            PERSISTENT
        }
    }
}
