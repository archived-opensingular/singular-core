package org.opensingular.server.commons.spring;

import org.apache.wicket.Session;
import org.opensingular.server.commons.util.SingularCache;
import org.opensingular.server.commons.util.SingularSessionCache;
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

    public static final String WICKET_SESSION_CACHE_NAME = "wicketSession";

    @Inject
    private CacheManager cacheManager;


    @Override
    public Cache getCache(String name) {
        if (cacheEnabled()) {
            return cacheManager.getCache(WICKET_SESSION_CACHE_NAME);
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
        cacheManager.getCache(WICKET_SESSION_CACHE_NAME).clear();
    }
}
