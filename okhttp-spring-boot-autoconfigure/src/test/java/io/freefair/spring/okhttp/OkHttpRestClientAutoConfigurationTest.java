package io.freefair.spring.okhttp;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "okhttp.read-timeout=21s",
                "okhttp.connect-timeout=21s",
                "okhttp.write-timeout=21s"
        })
class OkHttpRestClientAutoConfigurationTest {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Test
    void testTimeouts() throws NoSuchFieldException, IllegalAccessException {
        RestClient restTemplate = restClientBuilder.build();

        OkHttpClient client = extractClient(restTemplate);

        assertThat(client.connectTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
        assertThat(client.readTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
        assertThat(client.writeTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
    }

    private OkHttpClient extractClient(RestClient restClient) throws NoSuchFieldException, IllegalAccessException {

        Field clientRequestFactoryField = restClient.getClass().getDeclaredField("clientRequestFactory");
        clientRequestFactoryField.setAccessible(true);
        ClientHttpRequestFactory requestFactory = (ClientHttpRequestFactory) clientRequestFactoryField.get(restClient);

        while (requestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
            Field field = AbstractClientHttpRequestFactoryWrapper.class.getDeclaredField("requestFactory");
            field.setAccessible(true);
            requestFactory = (ClientHttpRequestFactory) field.get(requestFactory);
        }

        assertThat(requestFactory).isInstanceOf(OkHttpClientRequestFactory.class);

        return ((OkHttpClientRequestFactory)requestFactory).okHttpClient();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class TestConfiguration {

    }
}
