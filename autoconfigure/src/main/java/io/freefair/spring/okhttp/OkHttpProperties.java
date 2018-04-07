package io.freefair.spring.okhttp;

import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static lombok.AccessLevel.NONE;

/**
 * @author Lars Grefer
 */
@Data
@ConfigurationProperties(prefix = "okhttp")
public class OkHttpProperties {

    /**
     * The default connect timeout for new connections.
     */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /**
     * The default read timeout for new connections.
     */
    private Duration readTimeout = Duration.ofSeconds(10);

    /**
     * The default write timeout for new connections.
     */
    private Duration writeTimeout = Duration.ofSeconds(10);

    /**
     * The interval between web socket pings initiated by this client. Use this to
     * automatically send web socket ping frames until either the web socket fails or it is closed.
     * This keeps the connection alive and may detect connectivity failures early. No timeouts are
     * enforced on the acknowledging pongs.
     *
     * <p>The default value of 0 disables client-initiated pings.
     */
    private Duration pingInterval = Duration.ZERO;

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

    /**
     * @author Lars Grefer
     */
    @Data
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

        /**
         * @author Lars Grefer
         */
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
