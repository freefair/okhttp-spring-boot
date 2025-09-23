package io.freefair.spring.okhttp;

import okhttp3.Headers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OkHttpUtilsTest {


    @Test
    void headerConversion() {

        HttpHeaders springHeaders =  new HttpHeaders();
        Instant date = Instant.now();

        springHeaders.set("key1", "value1");
        springHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        springHeaders.setDate(date);

        Headers okHttpHeaders = OkHttpUtils.toOkHttpHeaders(springHeaders);

        assertThat(okHttpHeaders.get("key1")).isEqualTo("value1");
        assertThat(okHttpHeaders.getInstant("Date")).isCloseTo(date, Assertions.within(1, ChronoUnit.SECONDS));
        assertThat(okHttpHeaders.get("Accept-Charset")).isEqualToIgnoringCase("UTF-8");

        assertThat(OkHttpUtils.toSpringHeaders(okHttpHeaders)).isEqualTo(springHeaders);

    }

}
