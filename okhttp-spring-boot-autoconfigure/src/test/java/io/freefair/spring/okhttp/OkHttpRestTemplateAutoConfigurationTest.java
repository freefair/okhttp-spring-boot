package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static io.freefair.spring.okhttp.OkHttpTestUtils.extractClient;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("removal")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "okhttp.read-timeout=21s",
                "okhttp.connect-timeout=21s",
                "okhttp.write-timeout=21s"
        })
@Deprecated(forRemoval = true, since = "4.2.0")
class OkHttpRestTemplateAutoConfigurationTest {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Test
    void testTimeouts() {
        RestTemplate restTemplate = restTemplateBuilder.connectTimeout(Duration.ofSeconds(42)).build();

        OkHttpClient client = extractClient(restTemplate);

        assertThat(client.connectTimeoutMillis()).isEqualTo(Duration.ofSeconds(42).toMillis());
        assertThat(client.readTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
        assertThat(client.writeTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class TestConfiguration {

    }
}
