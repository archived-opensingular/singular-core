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

package org.opensingular.server.core.wicket;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class ModuleLink extends Link<Object> {

    private String url;

    public ModuleLink(String id, IModel<?> model, String url) {
        super(id);
        setBody(model);
        this.url = url;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        add($b.attr("target", "_blank"));
    }

    @Override
    public void onClick() {}

    @Override
    protected boolean getStatelessHint() {
        return true;
    }

    @Override
    protected CharSequence getURL() {
        return url;
    }
}
