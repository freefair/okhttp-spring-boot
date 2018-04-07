package io.freefair.spring.okhttp;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Lars Grefer
 */
public class OkHttp3AutoConfigurationTest {

    private ApplicationContextRunner applicationContextRunner;

    @Before
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(OkHttp3AutoConfiguration.class));
    }

    @Test
    public void testDefaultClient() {
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(OkHttpClient.class);
            assertThat(context.getBean(OkHttpClient.class).cache()).isNotNull();
        });
    }

    @Test
    public void testNoCache() {
        applicationContextRunner
                .withPropertyValues("okhttp.cache.max-size=0")
                .run(context -> {
            assertThat(context).hasSingleBean(OkHttpClient.class);

            assertThat(context.getBean(OkHttpClient.class).cache()).isNull();
        });
    }

    @Test
    public void testCustomCache() {
        applicationContextRunner
                .withUserConfiguration(CustomCacheConfiguration.class)
                .run(context -> {
            assertThat(context).hasSingleBean(OkHttpClient.class);

            assertThat(context.getBean(OkHttpClient.class).cache()).isEqualTo(CustomCacheConfiguration.CACHE);
        });
    }

    @Configuration
    static class CustomCacheConfiguration {

        static final Cache CACHE = new Cache(new File(""), 1);

        @Bean
        public Cache okHttp3Cache() {
            return CACHE;
        }
    }
}
