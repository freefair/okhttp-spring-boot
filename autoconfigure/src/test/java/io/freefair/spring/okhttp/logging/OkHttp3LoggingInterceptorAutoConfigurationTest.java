package io.freefair.spring.okhttp.logging;

import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Lars Grefer
 */
public class OkHttp3LoggingInterceptorAutoConfigurationTest {

    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(OkHttp3LoggingInterceptorAutoConfiguration.class));
    }

    @Test
    public void testDefaultLogger() {
        applicationContextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(HttpLoggingInterceptor.class);

                    assertThat(context).getBean(HttpLoggingInterceptor.class)
                            .hasFieldOrPropertyWithValue("logger", HttpLoggingInterceptor.Logger.DEFAULT);
                });
    }

    @Test
    public void testCustomLogger() {

        applicationContextRunner
                .withUserConfiguration(CustomLoggerConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(HttpLoggingInterceptor.class);

                    assertThat(context).getBean(HttpLoggingInterceptor.class)
                            .hasFieldOrPropertyWithValue("logger", CustomLoggerConfiguration.LOGGER);
                });
    }

    @Configuration
    static class CustomLoggerConfiguration {

        public static final HttpLoggingInterceptor.Logger LOGGER = message -> { };

        @Bean
        public HttpLoggingInterceptor.Logger customLogger() {
            return LOGGER;
        }
    }
}
