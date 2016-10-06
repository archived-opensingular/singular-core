/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.application;

import org.apache.wicket.request.IUrlRenderer;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.UrlRenderer;
import org.apache.wicket.util.string.Strings;

public class HttpsOnlyUrlRenderer extends UrlRenderer {

    private final Request request;

    public HttpsOnlyUrlRenderer(Request request) {
        super(request);
        this.request = request;
    }

    @SuppressWarnings("deprecation")
    public String renderFullUrl(final Url url) {
        if (url instanceof IUrlRenderer) {
            IUrlRenderer renderer = (IUrlRenderer) url;
            return renderer.renderFullUrl(url, getBaseUrl());
        }

        final String protocol = "https";
        final String host = resolveHost(url);

        final String path;
        if (url.isContextAbsolute()) {
            path = url.toString();
        } else {
            Url base = new Url(getBaseUrl());
            base.resolveRelative(url);
            path = base.toString();
        }

        StringBuilder render = new StringBuilder();
        if (!Strings.isEmpty(protocol)) {
            render.append(protocol);
            render.append(':');
        }

        if (!Strings.isEmpty(host)) {
            render.append("//");
            render.append(host);
        }

        if (!url.isContextAbsolute()) {
            render.append(request.getContextPath());
            render.append(request.getFilterPath());
        }
        return Strings.join("/", render.toString(), path);
    }

}
