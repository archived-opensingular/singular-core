package org.opensingular.server.commons.cache;

import org.apache.wicket.Session;
import org.opensingular.server.commons.cache.SingularCache;
import org.opensingular.server.commons.cache.SingularSessionCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collection;

/**
 * Cache manager proxy para que o cache dure apenas a sessão http do usuário
 * Se o cache for utilizado fora de uma sessão http seus valores não serão cacheados.
 */
@Named("wicketSessionCacheManager")
public class WicketSessionCacheManager implements CacheManager {

    @Inject
    private CacheManager cacheManager;

    @Override
    public Cache getCache(String name) {
        if (cacheEnabled()) {
            return cacheManager.getCache(SingularSessionCache.SINGULAR_CACHE_SESSION_CACHE);
        } else {
            return cacheManager.getCache(SingularCache.SINGULAR_CACHE_NAME);
        }
    }

    private boolean cacheEnabled() {
        return Session.exists();
    }

    @Override
    public Collection<String> getCacheNames() {
        return Arrays.asList(new String[]{SingularSessionCache.SINGULAR_CACHE_SESSION_CACHE});
    }

    public void clearCache() {
        cacheManager.getCache(SingularSessionCache.SINGULAR_CACHE_SESSION_CACHE).clear();
    }
}
