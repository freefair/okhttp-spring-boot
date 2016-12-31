package io.freefair.spring.okhttp;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A {@link Qualifier} annotation.
 *
 * @author Lars Grefer
 * @see NetworkInterceptor
 */
@Target({METHOD, FIELD, CONSTRUCTOR})
@Retention(RUNTIME)
@Qualifier
public @interface ApplicationInterceptor {
}
