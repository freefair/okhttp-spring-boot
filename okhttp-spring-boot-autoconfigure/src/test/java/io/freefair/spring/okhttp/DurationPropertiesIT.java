package io.freefair.spring.okhttp;

import io.freefair.spring.okhttp.autoconfigure.OkHttpProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Lars Grefer
 */
@SpringBootTest(
        properties = {
                "okhttp.connectTimeout=10s",
                "okhttp.readTimeout=1d",
                "okhttp.writeTimeout=5"
        }
)
public class DurationPropertiesIT {

    @Autowired
    private OkHttpProperties okHttpProperties;

    @Test
    public void getConnectTimeout() {
        assertThat(okHttpProperties.getConnectTimeout()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    public void getReadTimeout() {
        assertThat(okHttpProperties.getReadTimeout()).isEqualTo(Duration.ofDays(1));
    }

    @Test
    public void getWriteTimeout() {
        assertThat(okHttpProperties.getWriteTimeout()).isEqualTo(Duration.ofMillis(5));
    }

    @SpringBootConfiguration
    @EnableConfigurationProperties(OkHttpProperties.class)
    static class Config {
    }
}
