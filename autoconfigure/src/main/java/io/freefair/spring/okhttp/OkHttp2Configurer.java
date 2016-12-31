package io.freefair.spring.okhttp;

import com.squareup.okhttp.OkHttpClient;

/**
 * @author Lars Grefer
 */
public interface OkHttp2Configurer {

    void configure(OkHttpClient builder);
}
