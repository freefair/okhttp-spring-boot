package io.freefair.spring.okhttp;

/**
 * @author Lars Grefer
 */
public interface Configurer<T> {

    void configure(T object);
}
