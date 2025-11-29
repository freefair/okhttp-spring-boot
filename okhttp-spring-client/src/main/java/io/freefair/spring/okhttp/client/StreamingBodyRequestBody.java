package io.freefair.spring.okhttp.client;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jspecify.annotations.Nullable;
import org.springframework.http.StreamingHttpOutputMessage;

import java.io.IOException;

/**
 * {@link StreamingHttpOutputMessage.Body} based {@link RequestBody} implementation.
 *
 * @author Lars Grefer
 * @see OkHttpClientRequest
 */
@RequiredArgsConstructor
class StreamingBodyRequestBody extends RequestBody {

    private final StreamingHttpOutputMessage.Body streamingBody;

    private final MediaType contentType;

    @Getter(onMethod_=@Override) @Accessors(fluent = true) private final long contentLength;

    @Override
    public @Nullable MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        streamingBody.writeTo(bufferedSink.outputStream());
    }

    @Override
    public boolean isOneShot() {
        return !streamingBody.repeatable();
    }
}
