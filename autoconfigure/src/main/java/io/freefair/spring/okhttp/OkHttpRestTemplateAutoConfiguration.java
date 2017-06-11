package io.freefair.spring.okhttp;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author Lars Grefer
 */
@Configuration
@ConditionalOnClass({RestTemplateCustomizer.class, RestTemplate.class})
@AutoConfigureBefore(RestTemplateAutoConfiguration.class)
public class OkHttpRestTemplateAutoConfiguration {

    @Bean
    @Order(2)
    @ConditionalOnBean(OkHttp3ClientHttpRequestFactory.class)
    public RestTemplateCustomizer okhttp3RestTemplateCustomizer(OkHttp3ClientHttpRequestFactory requestFactory) {
        return restTemplate -> restTemplate.setRequestFactory(requestFactory);
    }

}
