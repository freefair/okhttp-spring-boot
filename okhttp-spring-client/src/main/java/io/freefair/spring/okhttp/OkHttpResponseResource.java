package io.freefair.spring.okhttp;

import okhttp3.Response;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URL;

public class OkHttpResponseResource extends OkHttpResponseBodyResource implements HttpMessage {

    private final Response response;

    public OkHttpResponseResource(Response response) {
        super(response.body());
        this.response = response;
    }

    @Override
    public String getDescription() {
        return "OkHttpResponse [ " + response + " ]";
    }

    @Override
    @Nullable
    public String getFilename() {

        String contentDisposition = response.header("Content-Disposition");
        if (StringUtils.hasText(contentDisposition)) {
            return ContentDisposition.parse(contentDisposition).getFilename();
        }

        return super.getFilename();
    }

    @Override
    public URL getURL() {
        return response.request().url().url();
    }

    @Override
    public URI getURI() {
        return response.request().url().uri();
    }

    @Override
    public HttpHeaders getHeaders() {
        return OkHttpUtils.toSpringHeaders(response.headers());
    }
}
