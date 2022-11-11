package io.freefair.spring.okhttp;

import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer restTemplateBuilderConfigurer,
                                                   OkHttpClient okHttpClient) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder = builder.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(okHttpClient));
        return restTemplateBuilderConfigurer.configure(builder);
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
