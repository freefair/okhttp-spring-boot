package io.freefair.spring.okhttp.autoconfigure;

import lombok.Data;
import okhttp3.Protocol;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.time.Duration;
import java.util.List;

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

    @NestedConfigurationProperty
    private CacheProperties cache = new CacheProperties();

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
     * Configure the protocols used by this client to communicate with remote servers.
     */
    @Nullable
    private List<Protocol> protocols = null;

    @NestedConfigurationProperty
    private final ConnectionPoolProperties connectionPool = new ConnectionPoolProperties();

    /**
     * @author Lars Grefer
     * @see okhttp3.Cache
     */
    @Data
    public static class CacheProperties {

        private boolean enabled;

        /**
         * The maximum number of bytes this cache should use to store.
         */
        private DataSize maxSize = DataSize.ofMegabytes(10);

        /**
         * The path of the directory where the cache should be stored.
         */
        @Nullable
        private File directory;
    }

    /**
     * @see okhttp3.ConnectionPool
     */
    @Data
    public static class ConnectionPoolProperties {

        /**
         * The maximum number of idle connections for each address.
         */
        private int maxIdleConnections = 5;

        private Duration keepAliveDuration = Duration.ofMinutes(5);
    }
}
