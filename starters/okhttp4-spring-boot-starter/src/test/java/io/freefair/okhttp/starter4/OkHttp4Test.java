package io.freefair.okhttp.starter4;

import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OkHttp4Test {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void hasClientBean() {
        AssertableApplicationContext context = AssertableApplicationContext.get(() -> applicationContext);

        assertThat(context).hasSingleBean(OkHttpClient.class);
        OkHttpClient okHttpClient = context.getBean(OkHttpClient.class);

        assertThat(okHttpClient).isNotNull();
    }
}
