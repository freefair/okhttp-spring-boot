package io.freefair.spring.okhttp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;
import org.springframework.core.io.AbstractResource;
import org.springframework.lang.Nullable;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@RequiredArgsConstructor
public class OkHttpResponseBodyResource extends AbstractResource implements Closeable {

    @Nullable
    @Getter
    private final ResponseBody responseBody;
    private boolean closed = false;

    @Override
    public byte[] getContentAsByteArray() throws IOException {
        if (responseBody == null) {
            throw new FileNotFoundException("Response body is null");
        }
        return responseBody.bytes();
    }

    @Override
    public String getContentAsString(Charset charset) throws IOException {
        if (responseBody == null) {
            throw new FileNotFoundException("Response body is null");
        }
        return responseBody.string();
    }

    @Override
    public String getDescription() {
        return "OkHttp ResponseBody " + responseBody;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        if (responseBody == null) {
            throw new FileNotFoundException("Response body is null");
        }
        return responseBody.byteStream();
    }

    @Override
    public long contentLength() throws FileNotFoundException {
        if (responseBody == null) {
            throw new FileNotFoundException("Response body is null");
        }
        return responseBody.contentLength();
    }

    @Override
    public boolean exists() {
        return responseBody != null;
    }

    @Override
    public boolean isOpen() {
        return exists() && !closed;
    }

    @Override
    public boolean isReadable() {
        return isOpen();
    }

    @Override
    public void close() {
        if (responseBody != null) {
            responseBody.close();
        }
        closed = true;
    }
}
