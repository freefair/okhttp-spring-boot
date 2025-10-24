package io.freefair.spring.okhttp;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OkHttpResponseResourceTest {

    @LocalServerPort
    private int port;

    private OkHttpClient client;

    @BeforeEach
    public void setUp() {
        client = new OkHttpClient.Builder().build();
    }

    @Test
    void testResource() throws IOException {

        Request request = new Request.Builder().url("http://localhost:" + port + "/echo")
                .post(RequestBody.create("Hello World", MediaType.parse("text/plain")))
                .addHeader("Content-Disposition", ContentDisposition.attachment().filename("foo.txt").build().toString())
                .build();

        try (Response execute = client.newCall(request).execute()){

            Resource resource = new OkHttpResponseResource(execute);

            assertThat(resource.isFile()).isFalse();
            assertThat(resource.exists()).isTrue();
            assertThat(resource.getFilename()).isEqualTo("foo.txt");
            assertThat(resource.contentLength()).isEqualTo(11);
            assertThat(resource.getContentAsString(StandardCharsets.UTF_8)).isEqualTo("Hello World");

        }
    }

}
