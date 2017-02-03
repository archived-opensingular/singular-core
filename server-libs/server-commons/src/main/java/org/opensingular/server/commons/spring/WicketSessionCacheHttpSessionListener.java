package org.opensingular.server.commons.spring;

import org.opensingular.lib.support.spring.util.ApplicationContextProvider;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import java.util.Optional;

@WebListener
public class WicketSessionCacheHttpSessionListener implements javax.servlet.http.HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Optional.ofNullable(ApplicationContextProvider.get()).map(ac -> ac.getBean(WicketSessionCacheManager.class)).ifPresent(wscm -> wscm.clearCache());
    }
}
