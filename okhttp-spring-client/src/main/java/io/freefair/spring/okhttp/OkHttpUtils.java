package io.freefair.spring.okhttp;

import kotlin.Pair;
import lombok.experimental.UtilityClass;
import okhttp3.Headers;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

@UtilityClass
public class OkHttpUtils {

    public static HttpHeaders toSpringHeaders(Headers okhttpHeaders) {
        Assert.notNull(okhttpHeaders, "Headers must not be null");
        HttpHeaders springHeaders = new HttpHeaders();

        for (Pair<? extends String, ? extends String> okhttpHeader : okhttpHeaders) {
            springHeaders.add(okhttpHeader.getFirst(), okhttpHeader.getSecond());
        }

        return springHeaders;
    }

    public static Headers toOkHttpHeaders(HttpHeaders springHeaders) {
        Headers.Builder builder = new Headers.Builder();

        springHeaders.forEach((name, values) -> {
            values.forEach(value -> builder.add(name, value));
        });

        return builder.build();
    }
}
