package io.freefair.spring.okhttp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OkHttpClientRequestFactoryTest {

    RestTemplate restTemplate;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplateBuilder.build();
    }

    @Test
    void get() {
        String response = restTemplate.getForObject("https://httpbin.org/get", String.class);

        assertThat(response).contains("okhttp");
    }

    @Test
    void put() {
        restTemplate.put("https://httpbin.org/put", "foo");
    }

    @Test
    void post() {
        String response = restTemplate.postForObject("https://httpbin.org/post", "foobar", String.class);

        assertThat(response).contains("foobar");
    }

    @Test
    void post_empty() {
        String response = restTemplate.postForObject("https://httpbin.org/post", null, String.class);

        assertThat(response).contains("headers");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class Config {

    }
}
