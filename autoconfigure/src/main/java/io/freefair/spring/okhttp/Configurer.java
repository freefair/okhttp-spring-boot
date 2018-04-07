package io.freefair.spring.okhttp;

/**
 * @author Lars Grefer
 */
@FunctionalInterface
public interface Configurer<T> {

    void configure(T object);
}
