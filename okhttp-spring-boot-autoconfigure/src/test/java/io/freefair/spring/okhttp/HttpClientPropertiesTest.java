package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static io.freefair.spring.okhttp.OkHttpTestUtils.extractClient;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.http.clients.connect-timeout=12s",
        "spring.http.clients.read-timeout=13s",
        "spring.http.clients.redirects=dont_follow"
})
public class HttpClientPropertiesTest {


    @Test
    void testRestTemplateConfig(@Autowired RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        OkHttpClient client = extractClient(restTemplate);

        testOkHttpClientConfig(client);
    }

    @Test
    void testRestClientConfig(@Autowired RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder.build();

        OkHttpClient client = extractClient(restClient);

        testOkHttpClientConfig(client);
    }

    private static void testOkHttpClientConfig(OkHttpClient client) {
        assertThat(client.connectTimeoutMillis()).isEqualTo(Duration.ofSeconds(12).toMillis());
        assertThat(client.readTimeoutMillis()).isEqualTo(Duration.ofSeconds(13).toMillis());
        assertThat(client.followRedirects()).isFalse();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class TestConfiguration {

    }

}
