package io.freefair.spring.okhttp.metrics;

import io.freefair.spring.okhttp.OkHttpProperties;
import io.micrometer.binder.okhttp3.OkHttpConnectionPoolMetrics;
import io.micrometer.binder.okhttp3.OkHttpMetricsEventListener;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfiguration(after = CompositeMeterRegistryAutoConfiguration.class)
@ConditionalOnBean(MeterRegistry.class)
@ConditionalOnClass({MeterRegistry.class, OkHttpMetricsEventListener.class, OkHttpClient.class})
@EnableConfigurationProperties(OkHttpMetricsProperties.class)
public class OkHttpMetricsAutoConfiguration {

    @Autowired
    private OkHttpMetricsProperties properties;

    @Bean
    @ConditionalOnProperty(value = "okhttp.metrics.enabled", matchIfMissing = true, havingValue = "true")
    @ConditionalOnMissingBean
    public OkHttpMetricsEventListener okHttpMetricsEventListener(MeterRegistry meterRegistry) {
        OkHttpMetricsEventListener.Builder builder = OkHttpMetricsEventListener
                .builder(meterRegistry, properties.getName())
                .includeHostTag(properties.isIncludeHostTag());

        List<String> requestTagKeys = properties.getRequestTagKeys();
        if (!CollectionUtils.isEmpty(requestTagKeys)) {
            builder = builder.requestTagKeys(requestTagKeys);
        }

        Map<String, String> tags = properties.getTags();
        if (!CollectionUtils.isEmpty(tags)) {
            builder = builder.tags(getTags(tags));
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnProperty(value = "okhttp.metrics.pool.enabled", matchIfMissing = true, havingValue = "true")
    @ConditionalOnBean(ConnectionPool.class)
    @ConditionalOnMissingBean
    public OkHttpConnectionPoolMetrics okHttpConnectionPoolMetrics(ConnectionPool connectionPool, OkHttpProperties okHttpProperties) {
        OkHttpMetricsProperties.ConnectionPoolMetricsProperties poolProperties = properties.getPool();
        return new OkHttpConnectionPoolMetrics(connectionPool,
                poolProperties.getNamePrefix(),
                getTags(poolProperties.getTags()),
                okHttpProperties.getConnectionPool().getMaxIdleConnections()
        );
    }

    private List<Tag> getTags(Map<String, String> tags) {
        return tags.entrySet()
                .stream()
                .map(entry -> Tag.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
