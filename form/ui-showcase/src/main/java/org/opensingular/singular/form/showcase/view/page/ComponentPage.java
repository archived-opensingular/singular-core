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

package org.opensingular.singular.form.showcase.view.page;

import org.opensingular.singular.form.showcase.view.template.Content;
import org.opensingular.singular.form.showcase.view.template.Template;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

@MountPath("component/component")
@SuppressWarnings("serial")
public class ComponentPage extends Template {

    private String componentName;

    public ComponentPage(PageParameters parameters) {
        this.componentName = parameters.get("cn").toString();
        if (componentName == null) {
            throw new RestartResponseAtInterceptPageException(getApplication().getHomePage());
        }
    }

    @Override
    protected Content getContent(String id) {
        return new ComponentContent(id, $m.ofValue(componentName));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemShowCase').addClass('active');"));
    }
}