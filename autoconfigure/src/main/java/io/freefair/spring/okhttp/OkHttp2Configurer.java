package io.freefair.spring.okhttp;

import com.squareup.okhttp.OkHttpClient;

/**
 * @author Lars Grefer
 */
@FunctionalInterface
public interface OkHttp2Configurer extends Configurer<OkHttpClient> {

}
