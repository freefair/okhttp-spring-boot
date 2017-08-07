package io.freefair.spring.okhttp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpMarkerConfiguration {

    @Bean
    public Marker markerBean() {
        return new Marker();
    }

    class Marker {

    }
}
