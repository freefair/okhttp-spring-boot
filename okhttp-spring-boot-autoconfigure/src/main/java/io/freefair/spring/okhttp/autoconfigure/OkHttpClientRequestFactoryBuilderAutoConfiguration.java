package io.freefair.spring.okhttp.autoconfigure;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author Lars Grefer
 * @see RestTemplateAutoConfiguration
 */
@AutoConfiguration(before = RestTemplateAutoConfiguration.class, after = HttpMessageConvertersAutoConfiguration.class)
@ConditionalOnClass({ClientHttpRequestFactoryBuilder.class, OkHttpClientRequestFactory.class})
public class OkHttpClientRequestFactoryBuilderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactoryBuilder.class)
    public OkHttpClientRequestFactoryBuilder okHttpClientRequestFactoryBuilder(OkHttpClient okHttpClient) {
        return new OkHttpClientRequestFactoryBuilder(okHttpClient);
    }

}
