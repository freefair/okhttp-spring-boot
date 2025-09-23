package io.freefair.spring.okhttp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@SpringBootApplication
@RestController
public class TestServer {

    @RequestMapping("/echo")
    public void echo(HttpServletRequest request, HttpServletResponse response) throws IOException {

        request.getHeaderNames().asIterator().forEachRemaining(header -> {
            request.getHeaders(header).asIterator().forEachRemaining(value -> {
                response.addHeader(header, value);
            });
        });

        FileCopyUtils.copy(request.getInputStream(), response.getOutputStream());

    }
}
