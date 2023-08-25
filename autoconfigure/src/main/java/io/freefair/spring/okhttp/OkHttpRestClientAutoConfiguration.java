package io.freefair.spring.okhttp;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.client.RestClient;

/**
 * @author Lars Grefer
 * @see RestClientAutoConfiguration
 */
@AutoConfiguration
@ConditionalOnClass({RestClientCustomizer.class, RestClient.class})
@Conditional(NotReactiveWebApplicationCondition.class)
public class OkHttpRestClientAutoConfiguration {

    @Bean
    public RestClientCustomizer okHttpRestClientCustomizer(OkHttpClient okHttpClient) {
        return restClientBuilder -> restClientBuilder.requestFactory(new OkHttpClientRequestFactory(okHttpClient));
    }

}
