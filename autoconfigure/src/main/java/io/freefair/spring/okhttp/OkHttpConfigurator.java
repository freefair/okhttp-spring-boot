package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;

public interface OkHttpConfigurator {

    void configure(OkHttpClient.Builder builder);
}
