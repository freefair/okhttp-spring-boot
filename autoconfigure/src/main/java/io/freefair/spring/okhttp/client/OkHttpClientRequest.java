package io.freefair.spring.okhttp.client;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import okio.Buffer;
import okio.ByteString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * OkHttp based {@link ClientHttpRequest} implementation.
 *
 * @author Lars Grefer
 * @see OkHttpClientRequestFactory
 */
@RequiredArgsConstructor
class OkHttpClientRequest extends AbstractClientHttpRequest implements StreamingHttpOutputMessage {

    private final OkHttpClient okHttpClient;

    private final URI uri;

    private final HttpMethod method;


    @Nullable
    private Body streamingBody;

    @Nullable
    private Buffer bufferBody;


    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public void setBody(Body body) {
        Assert.notNull(body, "body must not be null");
        assertNotExecuted();
        Assert.state(bufferBody == null, "getBody has already been used.");
        this.streamingBody = body;
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) {
        Assert.state(this.streamingBody == null, "setBody has already been used.");

        if (bufferBody == null) {
            bufferBody = new Buffer();
        }

        return bufferBody.outputStream();
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {

        Request okHttpRequest = buildRequest(headers);

        Response okHttpResponse = this.okHttpClient.newCall(okHttpRequest).execute();

        return new OkHttpClientResponse(okHttpResponse);
    }

    private Request buildRequest(HttpHeaders headers) throws MalformedURLException {

        Request.Builder builder = new Request.Builder();

        builder.url(uri.toURL());

        MediaType contentType = null;

        String contentTypeHeader = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.hasText(contentTypeHeader)) {
            contentType = MediaType.parse(contentTypeHeader);
        }

        RequestBody body = null;

        if (bufferBody != null) {
            ByteString bodyData = bufferBody.readByteString();
            if (headers.getContentLength() < 0) {
                headers.setContentLength(bodyData.size());
            }
            body = RequestBody.create(bodyData, contentType);
        } else if (streamingBody != null) {
            body = new StreamingBodyRequestBody(streamingBody, contentType, headers.getContentLength());
        } else if (okhttp3.internal.http.HttpMethod.requiresRequestBody(method.name())) {
            body = RequestBody.create(new byte[0], contentType);
        }

        builder.method(getMethod().name(), body);

        headers.forEach((name, values) -> {
            for (String value : values) {
                builder.addHeader(name, value);
            }
        });

        return builder.build();


    }

}
