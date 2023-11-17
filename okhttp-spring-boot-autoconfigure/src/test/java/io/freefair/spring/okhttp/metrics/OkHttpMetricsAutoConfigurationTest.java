package io.freefair.spring.okhttp.metrics;

import io.freefair.spring.okhttp.autoconfigure.metrics.OkHttpMetricsAutoConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.EventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class OkHttpMetricsAutoConfigurationTest {

    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        OkHttpMetricsAutoConfiguration.class,
                        MetricsAutoConfiguration.class,
                        CompositeMeterRegistryAutoConfiguration.class
                ));
    }

    @Test
    public void testEventListener() {
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(MeterRegistry.class);
            assertThat(context).hasSingleBean(EventListener.class);
        });
    }
}
