package io.freefair.spring.okhttp;

import com.squareup.okhttp.OkHttpClient;

public interface OkHttp2Configurator {

    void configure(OkHttpClient builder);
}
