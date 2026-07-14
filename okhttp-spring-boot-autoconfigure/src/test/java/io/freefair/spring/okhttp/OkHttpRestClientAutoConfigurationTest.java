package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static io.freefair.spring.okhttp.OkHttpTestUtils.extractClient;
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
    void testTimeouts() {
        RestClient restTemplate = restClientBuilder.build();

        OkHttpClient client = extractClient(restTemplate);

        assertThat(client.connectTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
        assertThat(client.readTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
        assertThat(client.writeTimeoutMillis()).isEqualTo(Duration.ofSeconds(21).toMillis());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class TestConfiguration {

    }
}
