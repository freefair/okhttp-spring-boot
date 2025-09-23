package io.freefair.spring.okhttp;

import okhttp3.MediaType;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;


class ResourceRequestBodyTest {


    @Test
    void testContentTypes() {

        ByteArrayResource resource = new ByteArrayResource(ByteString.encodeUtf8("hello world").toByteArray());

        assertThat(new ResourceRequestBody(resource).contentType())
                .isNull();

        assertThat(new ResourceRequestBody(resource, MimeTypeUtils.IMAGE_PNG).contentType().toString())
                .isEqualTo("image/png");

        assertThat(new ResourceRequestBody(resource, new MimeType("application", "foo", StandardCharsets.UTF_16)).contentType().toString())
                .isEqualTo("application/foo;charset=UTF-16");

        assertThat(new ResourceRequestBody(resource, MediaType.parse("text/plain")).contentType().toString())
                .isEqualTo("text/plain");

    }
}
