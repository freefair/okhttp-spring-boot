package io.freefair.spring.okhttp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpServerErrorException;
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
        try {
            String response = restTemplate.getForObject("https://httpbin.org/get", String.class);

            assertThat(response).contains("okhttp");
        } catch (HttpServerErrorException.ServiceUnavailable ignored) {
        }
    }

    @Test
    void put() {
        try {
            restTemplate.put("https://httpbin.org/put", "foo");
        } catch (HttpServerErrorException.ServiceUnavailable ignored) {
        }
    }

    @Test
    void post() {
        try {
            String response = restTemplate.postForObject("https://httpbin.org/post", "foobar", String.class);

            assertThat(response).contains("foobar");
        } catch (HttpServerErrorException.ServiceUnavailable ignored) {
        }
    }

    @Test
    void post_empty() {
        try {
            String response = restTemplate.postForObject("https://httpbin.org/post", null, String.class);

            assertThat(response).contains("headers");
        } catch (HttpServerErrorException.ServiceUnavailable ignored) {
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class Config {

    }
}
