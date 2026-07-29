package io.freefair.spring.okhttp;

import io.freefair.spring.okhttp.autoconfigure.OkHttp3AutoConfiguration;
import okhttp3.Cache;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


/**
 * @author Lars Grefer
 */
public class OkHttp3AutoConfigurationTest {

    private ApplicationContextRunner applicationContextRunner;

    @BeforeEach
    public void setUp() {
        applicationContextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(OkHttp3AutoConfiguration.class, SslAutoConfiguration.class));
    }

    @Test
    public void testDefaultClient() {
        applicationContextRunner.run(context -> {
            assertThat(context).hasSingleBean(OkHttpClient.class);
            assertThat(context.getBean(OkHttpClient.class).cache()).isNotNull();
            assertThat(context).doesNotHaveBean(Dns.class);
        });
    }

    @Test
    public void testDns() {
        applicationContextRunner
                .withUserConfiguration(DnsConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(OkHttpClient.class);
                    assertThat(context).hasSingleBean(Dns.class);

                    assertThat(context.getBean(OkHttpClient.class).dns()).isNotNull();
                });
    }

    @Test
    public void testNoCache() {
        applicationContextRunner
                .withPropertyValues("okhttp.cache.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(OkHttpClient.class);
                    assertThat(context).doesNotHaveBean(Cache.class);

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

    @Test
    public void testProtocols() {
        applicationContextRunner
                .withPropertyValues("okhttp.protocols=H2_PRIOR_KNOWLEDGE")
                .run(context -> {
                    assertThat(context).hasSingleBean(OkHttpClient.class);

                    List<Protocol> protocols = context.getBean(OkHttpClient.class).protocols();
                    assertThat(protocols).containsExactly(Protocol.H2_PRIOR_KNOWLEDGE);
                });
    }

    @Test
    public void testProtocols_invalid() {
        applicationContextRunner
                .withPropertyValues("okhttp.protocols=Foo")
                .run(context -> {
                    assertThat(context).hasFailed();
                });
    }

    @Test
    public void testProtocols_empty() {
        applicationContextRunner
                .withPropertyValues("okhttp.protocols=")
                .run(context -> {
                    assertThat(context).hasSingleBean(OkHttpClient.class);

                    List<Protocol> protocols = context.getBean(OkHttpClient.class).protocols();
                    assertThat(protocols).contains(Protocol.HTTP_1_1, Protocol.HTTP_2);
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

    @Configuration
    static class DnsConfiguration {
        @Bean
        public Dns dns() {
            return mock(Dns.class);
        }
    }
}
