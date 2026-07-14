package io.freefair.spring.okhttp.autoconfigure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.JettyClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.autoconfigure.imperative.ImperativeHttpClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OkHttpClientRequestFactoryBuilderAutoConfigurationTest {

    ApplicationContextRunner contextRunner;

    AutoConfigurations autoConfigurations;

    @BeforeEach
    void init() {
        contextRunner = new ApplicationContextRunner();
        autoConfigurations = AutoConfigurations.of(
                OkHttp3AutoConfiguration.class,
                OkHttpClientRequestFactoryBuilderAutoConfiguration.class,
                ImperativeHttpClientAutoConfiguration.class);
    }

    @Test
    void test_plain() {
        contextRunner
                .withConfiguration(autoConfigurations)
                .run(c -> {
                    assertTrue(c.containsBean("okHttpClientRequestFactoryBuilder"));
                    assertThat(c).getBean(ClientHttpRequestFactoryBuilder.class).isInstanceOf(OkHttpClientRequestFactoryBuilder.class);
                });
    }


    @Test
    void test_jetty() {
        contextRunner
                .withConfiguration(autoConfigurations)
                .withPropertyValues("spring.http.clients.imperative.factory=jetty")
                .run(c -> {
                    assertThat(c).getBean(ClientHttpRequestFactoryBuilder.class).isInstanceOf(JettyClientHttpRequestFactoryBuilder.class);
                });
    }
}
