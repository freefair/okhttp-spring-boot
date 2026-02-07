package io.freefair.spring.okhttp;

import lombok.val;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.freefair.spring.okhttp.OkHttpUtils.len;
import static io.freefair.spring.okhttp.OkHttpUtils.urlBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/*** @see OkHttpUtils */
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

    @Test
    void _builder () {
        HttpUrl.Builder b = urlBuilder("http:ya.ru/papa/:xyz/?q=x");
        b.scheme("https");
        b.host("yandex.com");
        b.port(8080);
        b.addPathSegments("mama");
        List<String> paths = b.getEncodedPathSegments$okhttp();
        for (int i = 0; i < paths.size(); i++){
            String pathElement = paths.get(i);
            if (":xyz".equalsIgnoreCase(pathElement)){
                b.setPathSegment(i, "42");// in reality, there is a search in the Map with variables
            }
        }
        b.addQueryParameter("foo", "bar");
        b.addQueryParameter("foo", "Zoo");
        String s = "https://yandex.com:8080/papa/42/mama?q=x&foo=bar&foo=Zoo";
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals(s, b.build().url().toString());
        assertEquals(s, OkHttpUtils.toString(b, false));
    }

    @Test
    void _toStringDemo () {
        var b = urlBuilder("http:/raw.net");
        String s = "http://raw.net/";
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals(s, OkHttpUtils.toString(b, true));
        assertEquals("http://raw.net", OkHttpUtils.toString(b, false));

        b = urlBuilder("http:/raw.net/");
        s = "http://raw.net/";
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals(s, OkHttpUtils.toString(b, true));
        assertEquals("http://raw.net", OkHttpUtils.toString(b, false));

        b = urlBuilder("https://with.port.org:42");
        s = "https://with.port.org:42/";
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals(s, OkHttpUtils.toString(b, true));
        assertEquals("https://with.port.org:42", OkHttpUtils.toString(b, false));

        b = urlBuilder("HTTPS:with.port.org:443");
        s = "https://with.port.org/";
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals(s, OkHttpUtils.toString(b, true));
        assertEquals("https://with.port.org", OkHttpUtils.toString(b, false));


        s = "https://example.com/foo/bar?x=y";
        b = urlBuilder(s);
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals("https://example.com/foo/bar/?x=y", OkHttpUtils.toString(b, true));
        assertEquals(s, OkHttpUtils.toString(b, false));

        s = "https://example.com/foo/bar/?x=y#boo";
        b = urlBuilder(s);
        assertEquals(s, b.toString());
        assertEquals(s, b.build().toString());
        assertEquals(s, OkHttpUtils.toString(b, true));
        assertEquals("https://example.com/foo/bar?x=y#boo", OkHttpUtils.toString(b, false));
    }

    @Test
    void _ourBuilderToString () {
        HttpUrl.Builder b = urlBuilder("http:ya.ru");
        assertEquals("http://ya.ru/", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));

        b.addPathSegments("foo");
        assertEquals("http://ya.ru/foo", b.toString());
        assertEquals("http://ya.ru/foo", OkHttpUtils.toString(b, false));

        b.addQueryParameter("q","text");
        assertEquals("http://ya.ru/foo?q=text", OkHttpUtils.toString(b, false));

        b.port(9042);
        assertEquals("http://ya.ru:9042/foo?q=text", OkHttpUtils.toString(b, false));

        b.removePathSegment(0);
        assertEquals("http://ya.ru:9042?q=text", OkHttpUtils.toString(b, false));

        for (int loop = 0; loop < 10_000; loop++){
            val builder = Instancio.create(HttpUrl.Builder.class);
            if ((len(builder.getEncodedQueryNamesAndValues$okhttp()) & 1) == 1){
                builder.getEncodedQueryNamesAndValues$okhttp().add(null);// value must exists
            }
            String url = builder.toString();
            if (url.endsWith("/")){
                url = url.substring(0, url.length()-1);// cut out last /
            }
            url = url.replace("/?", "?");
            assertEquals(url, OkHttpUtils.toString(builder, false));
        }
    }

    @Test
    void _ourBuilderToStringANTI () {
        HttpUrl.Builder b = urlBuilder("http:ya.ru");
        assertEquals("http://ya.ru/", b.toString());
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));

        b.addPathSegments("foo");
        assertEquals("http://ya.ru/foo", b.toString());
        assertEquals("http://ya.ru/foo/", OkHttpUtils.toString(b, true));

        b.addQueryParameter("q","text");
        assertEquals("http://ya.ru/foo/?q=text", OkHttpUtils.toString(b, true));

        b.port(9042);
        assertEquals("http://ya.ru:9042/foo/?q=text", OkHttpUtils.toString(b, true));

        b.removePathSegment(0);
        assertEquals("http://ya.ru:9042/?q=text", OkHttpUtils.toString(b, true));

        b.addQueryParameter("zzz", null);
        assertEquals("http://ya.ru:9042/?q=text&zzz", OkHttpUtils.toString(b, true));
    }

    @Test
    void _ourBuilderToStringTwoSlashes () {
        HttpUrl.Builder b = urlBuilder("http:ya.ru//");
        assertEquals("http://ya.ru//", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));
        b.addPathSegments("");
        assertEquals("http://ya.ru//", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));
        b.addPathSegments("");
        assertEquals("http://ya.ru//", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));
        b.addEncodedPathSegment("");
        assertEquals("http://ya.ru//", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));
        b.addEncodedPathSegments("");
        assertEquals("http://ya.ru//", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));

        b.port(80);// default http port ⇒ ignore
        assertEquals("http://ya.ru//", b.toString());
        assertEquals("http://ya.ru", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru/", OkHttpUtils.toString(b, true));
        b.port(8090);
        assertEquals("http://ya.ru:8090//", b.toString());
        assertEquals("http://ya.ru:8090", OkHttpUtils.toString(b, false));
        assertEquals("http://ya.ru:8090/", OkHttpUtils.toString(b, true));
    }

}
