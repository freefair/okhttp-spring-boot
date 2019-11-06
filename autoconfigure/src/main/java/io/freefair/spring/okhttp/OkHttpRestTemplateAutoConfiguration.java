package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lars Grefer
 * @see RestTemplateAutoConfiguration
 */
@Configuration
@ConditionalOnClass({RestTemplateCustomizer.class, RestTemplate.class})
@AutoConfigureBefore(RestTemplateAutoConfiguration.class)
@AutoConfigureAfter(HttpMessageConvertersAutoConfiguration.class)
public class OkHttpRestTemplateAutoConfiguration {

    private final ObjectProvider<HttpMessageConverters> messageConverters;

    private final ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers;

    public OkHttpRestTemplateAutoConfiguration(ObjectProvider<HttpMessageConverters> messageConverters,
                                         ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers) {
        this.messageConverters = messageConverters;
        this.restTemplateCustomizers = restTemplateCustomizers;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplateBuilder okHttpRestTemplateBuilder(OkHttpClient okHttpClient) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        HttpMessageConverters converters = this.messageConverters.getIfUnique();
        if (converters != null) {
            builder = builder.messageConverters(converters.getConverters());
        }

        List<RestTemplateCustomizer> customizers = this.restTemplateCustomizers.orderedStream()
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(customizers)) {
            builder = builder.customizers(customizers);
        }

        builder = builder.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(okHttpClient));

        return builder;
    }

}
