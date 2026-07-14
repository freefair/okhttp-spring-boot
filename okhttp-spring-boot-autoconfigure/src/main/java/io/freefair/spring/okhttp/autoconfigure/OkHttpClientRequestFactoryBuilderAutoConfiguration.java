package io.freefair.spring.okhttp.autoconfigure;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.autoconfigure.imperative.ImperativeHttpClientAutoConfiguration;
import org.springframework.boot.restclient.autoconfigure.RestTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Lars Grefer
 * @see RestTemplateAutoConfiguration
 */
@AutoConfiguration(before = ImperativeHttpClientAutoConfiguration.class)
@ConditionalOnClass({ClientHttpRequestFactoryBuilder.class, OkHttpClientRequestFactory.class})
public class OkHttpClientRequestFactoryBuilderAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "spring.http.clients.imperative.factory", havingValue = "okhttp", matchIfMissing = true)
    @ConditionalOnBean(OkHttpClient.class)
    @ConditionalOnMissingBean(ClientHttpRequestFactoryBuilder.class)
    public OkHttpClientRequestFactoryBuilder okHttpClientRequestFactoryBuilder(OkHttpClient okHttpClient) {
        return new OkHttpClientRequestFactoryBuilder(okHttpClient);
    }

}
