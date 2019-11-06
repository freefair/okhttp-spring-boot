package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "okhttp.read-timeout=21s",
                "okhttp.connect-timeout=21s",
                "okhttp.write-timeout=21s"
        })
class OkHttpRestTemplateAutoConfigurationTest {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Test
    void testTimeouts() throws NoSuchFieldException, IllegalAccessException {
        RestTemplate restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(42)).build();

        OkHttpClient client = extractClient(restTemplate);

        assertThat(client.connectTimeoutMillis()).isEqualTo(Duration.ofSeconds(42).toMillis());
        assertThat(client.readTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
        assertThat(client.writeTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
    }

    private OkHttpClient extractClient(RestTemplate restTemplate) throws NoSuchFieldException, IllegalAccessException {
        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();

        while (requestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
            Field field = AbstractClientHttpRequestFactoryWrapper.class.getDeclaredField("requestFactory");
            field.setAccessible(true);
            requestFactory = (ClientHttpRequestFactory) field.get(requestFactory);
        }

        assertThat(requestFactory).isInstanceOf(OkHttp3ClientHttpRequestFactory.class);

        Field field = OkHttp3ClientHttpRequestFactory.class.getDeclaredField("client");
        field.setAccessible(true);
        return (OkHttpClient) field.get(requestFactory);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class TestConfiguration {

    }
}
