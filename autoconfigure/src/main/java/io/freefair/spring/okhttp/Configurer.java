package io.freefair.spring.okhttp;

/**
 * @author Lars Grefer
 * @deprecated Use {@link OkHttp3Configurer} instead.
 */
@FunctionalInterface
@Deprecated
public interface Configurer<T> {

    void configure(T object);
}
