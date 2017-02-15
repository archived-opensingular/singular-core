package org.opensingular.server.commons.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Caches data  per wicket http session as long as the session is active.
 * if there is no wicket session it defaults to @SingularCache to cache and lookup
 */
@Cacheable(cacheNames = SingularSessionCache.SINGULAR_CACHE_SESSION_CACHE, unless = "#result == null", keyGenerator = "singularKeyGenerator", cacheManager = "wicketSessionCacheManager")
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SingularSessionCache {
    public static final String SINGULAR_CACHE_SESSION_CACHE = "wicketSession";
}
