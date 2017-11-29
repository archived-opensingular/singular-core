/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.servlet.KeepSessionAliveServlet;

import java.util.HashMap;
import java.util.Map;

public class KeepSessionAliveBehavior extends Behavior implements Loggable {

    public final static String KEEP_ALIVE_JS = "KeepSessionAliveBehavior.js";

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnDomReadyHeaderItem.forScript(getKeepAliveScript().asString(getKeepAliveParametersMap())));
    }

    private PackageTextTemplate getKeepAliveScript() {
        return new PackageTextTemplate(getClass(), KEEP_ALIVE_JS);
    }

    private Map<String, Object> getKeepAliveParametersMap() {
        final Map<String, Object> params = new HashMap<>();
        params.put("callbackUrl", getContextRelativePath());
        return params;
    }

    private String getContextRelativePath() {
        String path = WebApplication.get().getServletContext().getContextPath();
        if (!path.endsWith("/")) {
            path += "/";
        }
        if (KeepSessionAliveServlet.ENDPOINT.startsWith("/")) {
            path += KeepSessionAliveServlet.ENDPOINT.substring(1, KeepSessionAliveServlet.ENDPOINT.length());
        } else {
            path += KeepSessionAliveServlet.ENDPOINT;
        }
        return path;
    }

}