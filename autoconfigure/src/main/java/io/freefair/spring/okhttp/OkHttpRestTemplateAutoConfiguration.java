package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author Lars Grefer
 * @see RestTemplateAutoConfiguration
 */
@AutoConfiguration(before = RestTemplateAutoConfiguration.class, after = HttpMessageConvertersAutoConfiguration.class)
@ConditionalOnClass({RestTemplateCustomizer.class, RestTemplate.class})
@Conditional(OkHttpRestTemplateAutoConfiguration.NotReactiveWebApplicationCondition.class)
public class OkHttpRestTemplateAutoConfiguration {

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public RestTemplateBuilder restTemplateBuilder(ObjectProvider<HttpMessageConverters> messageConverters,
                                                   ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers,
                                                   ObjectProvider<RestTemplateRequestCustomizer<?>> restTemplateRequestCustomizers,
                                                   OkHttpClient okHttpClient) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        HttpMessageConverters converters = messageConverters.getIfUnique();
        if (converters != null) {
            builder = builder.messageConverters(converters.getConverters());
        }
        builder = addCustomizers(builder, restTemplateCustomizers, RestTemplateBuilder::customizers);
        builder = addCustomizers(builder, restTemplateRequestCustomizers, RestTemplateBuilder::requestCustomizers);

        builder = builder.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(okHttpClient));

        return builder;
    }

    private <T> RestTemplateBuilder addCustomizers(RestTemplateBuilder builder, ObjectProvider<T> objectProvider,
                                                   BiFunction<RestTemplateBuilder, Collection<T>, RestTemplateBuilder> method) {
        List<T> customizers = objectProvider.orderedStream().collect(Collectors.toList());
        if (!customizers.isEmpty()) {
            return method.apply(builder, customizers);
        }
        return builder;
    }

    static class NotReactiveWebApplicationCondition extends NoneNestedConditions {

        NotReactiveWebApplicationCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
        private static class ReactiveWebApplication {

        }

    }

}
