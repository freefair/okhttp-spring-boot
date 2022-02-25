package io.freefair.spring.okhttp.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import okhttp3.OkHttpClient;
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
@ConditionalOnProperty(value = "okhttp.metrics.enabled", matchIfMissing = true, havingValue = "true")
@ConditionalOnBean(MeterRegistry.class)
@ConditionalOnClass({MeterRegistry.class, OkHttpMetricsEventListener.class, OkHttpClient.class})
@EnableConfigurationProperties(OkHttpMetricsProperties.class)
public class OkHttpMetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OkHttpMetricsEventListener okHttpMetricsEventListener(MeterRegistry meterRegistry, OkHttpMetricsProperties properties) {
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

    private List<Tag> getTags(Map<String, String> tags) {
        return tags.entrySet()
                .stream()
                .map(entry -> Tag.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
