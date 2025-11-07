package io.freefair.spring.okhttp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OkHttpClientRequestFactoryTest {

    RestTemplate restTemplate;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplateBuilder
                .rootUri("http://localhost:" + port)
                .build();
    }

    @Test
    void get() {
        String response = restTemplate.getForObject("/user-agent", String.class);

        assertThat(response).contains("okhttp");
    }

    @Test
    void put() {
        restTemplate.put("/put", "foo");
    }

    @Test
    void post() {
        String response = restTemplate.postForObject("/post", "foobar", String.class);

        assertThat(response).contains("foobar");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @RestController
    static class Config {

        @GetMapping("/user-agent")
        public String getUserAgent(@RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
            return userAgent;
        }

        @PutMapping("/put")
        public String put(@RequestBody String body) {
            return body;
        }

        @PostMapping("/post")
        public String post(@RequestBody String body) {
            return body;
        }

    }
}
