package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;

public interface OkHttp3Configurator {

    void configure(OkHttpClient.Builder builder);
}
