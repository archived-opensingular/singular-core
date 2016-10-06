/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.application;

import org.apache.wicket.protocol.https.Scheme;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.util.lang.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Configura para que todos os redirects enviados pelo wicket sejam em https
 * mesmo que a aplicacao não esteja em https.
 * É utilizado caso a aplicacao esteja atras de um balanceador ou proxy
 * que implemente o https no lugar do servidor de aplicacao
 * @author vinicius
 *
 */
public class HttpsOnlyRequestMapper implements IRequestMapper {

    private static final Logger logger = LoggerFactory.getLogger(HttpsOnlyRequestMapper.class);
    private final IRequestMapper delegate;

    /**
     * Constructor
     * 
     * @param delegate
     */
    public HttpsOnlyRequestMapper(IRequestMapper delegate) {
        this.delegate = Args.notNull(delegate, "delegate");
    }

    @Override
    public final int getCompatibilityScore(Request request) {
        return delegate.getCompatibilityScore(request);
    }

    @Override
    public final IRequestHandler mapRequest(Request request) {
        IRequestHandler handler = delegate.mapRequest(request);
        if (handler instanceof RedirectRequestHandler) {
            return new HandlerWrapper((RedirectRequestHandler) handler);
        }
        return handler;
    }

    @Override
    public final Url mapHandler(IRequestHandler handler) {
        return mapHandler(handler, RequestCycle.get().getRequest());
    }

    private Url toHttps(Url url) {
        if (url != null) {
            url.setProtocol(Scheme.HTTPS.urlName());
        }
        return url;
    }

    /**
     * Creates a url for the handler. Modifies it with the correct
     * {@link Scheme} if necessary.
     * 
     * @param handler
     * @param request
     * @return url
     */
    final Url mapHandler(IRequestHandler handler, Request request) {
        Url url = delegate.mapHandler(handler);
        return toHttps(url);
    }

    public static class HandlerWrapper implements IRequestHandler {

        private final RedirectRequestHandler handler;

        public HandlerWrapper(RedirectRequestHandler handler) {
            this.handler = handler;
        }

        @Override
        public void respond(final IRequestCycle requestCycle) {
            String location;

            final String url = handler.getRedirectUrl();
           
            if (url.charAt(0) == '/') {
                // context-absolute url
                location = requestCycle.getUrlRenderer().renderContextRelativeUrl(url);
            } else {
                // if relative url, servlet container will translate to absolute
                // as
                // per the servlet spec
                // if absolute url still do the same
                location = url;
            }
            
            if (location.startsWith("http") && !location.startsWith("https")) {
                location = location.replaceFirst("http", "https");
            }
            location = location.replaceFirst(":80", "");
            logger.info(location);
            logger.info(url);
            
            
            WebResponse response = (WebResponse) requestCycle.getResponse();

            if (handler.getStatus() == HttpServletResponse.SC_MOVED_TEMPORARILY) {
                response.sendRedirect(location);
            } else {
                response.setStatus(handler.getStatus());
                response.setHeader("Location", location);
            }
        }

        @Override
        public void detach(IRequestCycle requestCycle) {
            handler.detach(requestCycle);

        }

    }
}
