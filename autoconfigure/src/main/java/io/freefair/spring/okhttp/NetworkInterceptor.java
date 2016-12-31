package io.freefair.spring.okhttp;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A {@link Qualifier} annotation for {@link com.squareup.okhttp.Interceptor OkHttp2-Interceptors} and {@link okhttp3.Interceptor OkHttp3-Interceptors}.
 *
 * @author Lars Grefer
 * @see ApplicationInterceptor
 */
@Target({METHOD, FIELD, CONSTRUCTOR})
@Retention(RUNTIME)
@Qualifier
public @interface NetworkInterceptor {
}
