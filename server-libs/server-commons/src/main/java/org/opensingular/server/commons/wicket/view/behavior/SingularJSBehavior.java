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

package org.opensingular.server.commons.wicket.view.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class SingularJSBehavior extends AbstractDefaultAjaxBehavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        String js =
                " Singular = Singular || {}; "
                        + " Singular.reloadContent = function () { "
                        + "     if (Singular && Singular.atualizarContadores) {"
                        + "           Singular.atualizarContadores(); "
                        + "       }    "
                        + getCallbackScript(getComponent())
                        + " }; ";
        response.render(OnDomReadyHeaderItem.forScript(js));
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        getComponent().getPage().visitChildren((component, visit) -> {
            if (component.getId().equals("tabela")) {
                target.add(component);
                visit.stop();
            }
        });
    }

}
