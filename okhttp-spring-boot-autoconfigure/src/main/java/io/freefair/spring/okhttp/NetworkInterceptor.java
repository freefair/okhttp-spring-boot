package io.freefair.spring.okhttp;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A {@link Qualifier} annotation for {@link okhttp3.Interceptor OkHttp3-Interceptors}.
 *
 * @author Lars Grefer
 * @see ApplicationInterceptor
 */
@SuppressWarnings("WeakerAccess")
@Target({METHOD, FIELD, CONSTRUCTOR, TYPE})
@Retention(RUNTIME)
@Qualifier
public @interface NetworkInterceptor {
}
