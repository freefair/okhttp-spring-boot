package io.freefair.spring.okhttp.client;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Lars Grefer
 */
public class ResourceRequestBody extends RequestBody {

    private final Resource resource;

    @Nullable
    private final MediaType mediaType;

    public ResourceRequestBody(Resource resource) {
        this.resource = resource;
        this.mediaType = null;
    }

    public ResourceRequestBody(Resource resource, MimeType springMimeType) {
        this.resource = resource;
        this.mediaType = MediaType.parse(springMimeType.toString());
    }

    public ResourceRequestBody(Resource resource, MediaType okhttpMediaType) {
        this.resource = resource;
        this.mediaType = okhttpMediaType;
    }

    @Override
    @Nullable
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            bufferedSink.writeAll(Okio.source(inputStream));
        }
    }

    @Override
    public long contentLength() throws IOException {
        if (resource instanceof InputStreamResource && resource.getClass().equals(InputStreamResource.class)) {
            return -1;
        }
        return resource.contentLength();
    }

    @Override
    public boolean isOneShot() {
        if (resource instanceof InputStreamResource) {
            return true;
        }

        if (resource instanceof ByteArrayResource) {
            return false;
        }

        if (resource instanceof FileSystemResource) {
            return false;
        }

        return super.isOneShot();
    }
}
