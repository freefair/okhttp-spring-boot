package io.freefair.spring.okhttp.ssl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ssl.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "server.ssl.enabled=true",
        "server.ssl.key-store=classpath:keystore.jks",
        "server.ssl.key-store-password=changeit",
        "server.ssl.key-alias=server-alias",
        "server.ssl.key-password=changeit",
        "spring.ssl.bundle.jks.a.truststore.location=classpath:keystore.jks",
        "spring.ssl.bundle.jks.a.truststore.password=changeit"
})
public class SslBundleTest {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private SslBundles sslBundles;

    @LocalServerPort
    private int port;

    @Test
    void testSsl() {

        SslBundle ssl = sslBundles.getBundle("a");

        RestTemplate restTemplate = restTemplateBuilder
                .sslBundle(ssl)
                .rootUri("https://localhost:" + port)
                .build();

        String result = restTemplate.getForObject("/foo", String.class);
        assertThat(result).isEqualTo("bar");

    }

    @Configuration
    @SpringBootApplication
    @RestController
    public static class TestController {


        @GetMapping("/foo")
        public String foo() {
            return "bar";
        }

        @Bean
        public HostnameVerifier hostnameVerifier() {
            return (hostname, session) -> true;
        }
    }

}
