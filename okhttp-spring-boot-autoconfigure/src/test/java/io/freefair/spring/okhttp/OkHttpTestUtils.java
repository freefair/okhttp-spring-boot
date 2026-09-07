package io.freefair.spring.okhttp;

import io.freefair.spring.okhttp.client.OkHttpClientRequestFactory;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import org.jspecify.annotations.NonNull;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class OkHttpTestUtils {

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true, since = "4.2.0")
    public static OkHttpClient extractClient(RestTemplate restTemplate) {
        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();

        return extractClient(requestFactory);
    }

    public static OkHttpClient extractClient(RestClient restClient) {

        Field clientRequestFactory = ReflectionUtils.findField(restClient.getClass(), "clientRequestFactory");
        ReflectionUtils.makeAccessible(clientRequestFactory);

        ClientHttpRequestFactory requestFactory = (ClientHttpRequestFactory) ReflectionUtils.getField(clientRequestFactory, restClient);

        return extractClient(requestFactory);
    }

    public static @NonNull OkHttpClient extractClient(ClientHttpRequestFactory requestFactory) {
        while (requestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
            requestFactory = ((AbstractClientHttpRequestFactoryWrapper) requestFactory).getDelegate();
        }

        assertThat(requestFactory).isInstanceOf(OkHttpClientRequestFactory.class);

        return ((OkHttpClientRequestFactory) requestFactory).okHttpClient();
    }
}
