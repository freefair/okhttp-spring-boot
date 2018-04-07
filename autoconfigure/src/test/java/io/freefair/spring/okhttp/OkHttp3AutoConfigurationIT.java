package io.freefair.spring.okhttp;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Lars Grefer
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OkHttp3AutoConfigurationIT {

    @Autowired
    private OkHttpClient okHttpClient;

    @Test
    public void testContextLoads() {
        assertThat(okHttpClient).isNotNull();
    }

    @Test
    public void testApplicationInterceptor() {
        assertThat(okHttpClient.interceptors()).size().isGreaterThan(0);

        for (Interceptor interceptor : okHttpClient.interceptors()) {
            if (interceptor instanceof MyInterceptor) {
                return;
            }
        }
        fail("MyInterceptor not found");
    }

    @Test
    public void testNetworkInterceptor() {
        assertThat(okHttpClient.networkInterceptors()).size().isGreaterThan(0);

        for (Interceptor interceptor : okHttpClient.networkInterceptors()) {
            if (interceptor instanceof MyInterceptor) {
                return;
            }
        }
        fail("MyInterceptor not found");
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {

        @Bean
        @ApplicationInterceptor
        public Interceptor appInterceptor() {
            return new MyInterceptor();
        }

        @Bean
        @NetworkInterceptor
        public Interceptor netInterceptor() {
            return new MyInterceptor();
        }
    }

    static class MyInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request());
        }
    }
}
