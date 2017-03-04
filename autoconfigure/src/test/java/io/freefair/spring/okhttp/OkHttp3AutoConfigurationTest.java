package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Lars Grefer
 */
@SpringBootTest(classes = OkHttp3AutoConfiguration.class)
@RunWith(SpringRunner.class)
public class OkHttp3AutoConfigurationTest {

    @Autowired
    OkHttpClient okHttpClient;

    @Test
    public void okHttp3Client() throws Exception {
        assertThat(okHttpClient).isNotNull();
    }

}
