package io.freefair.spring.okhttp.metrics;

import io.micrometer.core.instrument.binder.okhttp3.OkHttpConnectionPoolMetrics;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import lombok.Data;
import okhttp3.Request;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lars Grefer
 * @see OkHttpMetricsEventListener
 * @see OkHttpMetricsAutoConfiguration
 */
@Data
@ConfigurationProperties("okhttp.metrics")
public class OkHttpMetricsProperties {

    private boolean enabled = true;

    /**
     * Name for the metrics.
     */
    private String name = "okhttp";

    /**
     * Whether to include the {@code host} tag.
     *
     * @see OkHttpMetricsEventListener.Builder#includeHostTag(boolean)
     */
    private boolean includeHostTag = true;

    /**
     * Tag keys for {@link Request#tag()} or {@link Request#tag(Class)}.
     *
     * @see OkHttpMetricsEventListener.Builder#requestTagKeys(Iterable)
     */
    private List<String> requestTagKeys = new ArrayList<>();

    /**
     * @see OkHttpMetricsEventListener.Builder#tags(Iterable)
     */
    private Map<String, String> tags = new HashMap<>();

    @NestedConfigurationProperty
    private final ConnectionPoolMetricsProperties pool = new ConnectionPoolMetricsProperties();

    /**
     * @see OkHttpConnectionPoolMetrics
     */
    @Data
    public static class ConnectionPoolMetricsProperties {

        private boolean enabled = true;

        /**
         * @see OkHttpConnectionPoolMetrics#namePrefix
         */
        private String namePrefix = "okhttp.pool";

        /**
         * @see OkHttpConnectionPoolMetrics#tags
         */
        private Map<String, String> tags = new HashMap<>();
    }
}
