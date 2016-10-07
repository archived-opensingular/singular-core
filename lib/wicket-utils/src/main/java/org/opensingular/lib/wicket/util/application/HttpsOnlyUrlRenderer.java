/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.application;

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
