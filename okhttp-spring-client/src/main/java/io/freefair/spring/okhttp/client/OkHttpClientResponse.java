package io.freefair.spring.okhttp.client;

import io.freefair.spring.okhttp.OkHttpUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.InputStream;

/**
 * OkHttp based {@link ClientHttpResponse} implementation.
 *
 * @author Lars Grefer
 * @see OkHttpClientRequest
 * @see org.springframework.http.client.OkHttp3ClientHttpResponse
 */
@RequiredArgsConstructor
public class OkHttpClientResponse implements ClientHttpResponse {

    @Getter private final Response okHttpResponse;

    private HttpHeaders springHeaders;

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatusCode.valueOf(okHttpResponse.code());
    }

    @Override
    public String getStatusText() {
        return okHttpResponse.message();
    }

    @Override
    public void close() {
        ResponseBody body = okHttpResponse.body();
        if (body != null) {
            body.close();
        }
    }

    @Override
    public InputStream getBody() {
        ResponseBody body = okHttpResponse.body();
        if (body != null) {
            return body.byteStream();
        } else {
            return InputStream.nullInputStream();
        }
    }

    @Override
    public HttpHeaders getHeaders() {
        if (springHeaders == null) {
            springHeaders = OkHttpUtils.toSpringHeaders(okHttpResponse.headers());
        }

        return springHeaders;
    }

}
