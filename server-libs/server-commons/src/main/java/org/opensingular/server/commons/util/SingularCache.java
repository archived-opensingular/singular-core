package org.opensingular.server.commons.util;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple cache strategy with 2min duration.
 */
@Cacheable(cacheNames =  SingularCache.SINGULAR_CACHE_NAME, unless="#result == null", keyGenerator = "singularKeyGenerator")
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SingularCache {

    public static final String SINGULAR_CACHE_NAME = "defaultCache";
}
