package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;

/**
 * @author Lars Grefer
 */
@FunctionalInterface
public interface OkHttp3Configurer extends Configurer<OkHttpClient.Builder> {

}
