package io.freefair.spring.okhttp;

public interface Configurer<T> {

    void configure(T object);
}
