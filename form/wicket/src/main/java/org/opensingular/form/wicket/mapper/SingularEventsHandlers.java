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

package org.opensingular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.Arrays;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class SingularEventsHandlers extends Behavior {

    private final FUNCTION[] functions;

    public SingularEventsHandlers(FUNCTION... functions) {
        this.functions = functions;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(forReference(new PackageResourceReference(SingularEventsHandlers.class, "SingularEventsHandlers.js")));
        Arrays
                .stream(functions)
                .forEach( f -> response.render(OnDomReadyHeaderItem.forScript(f.getScript(component))));
    }

    public enum FUNCTION {

        /**
         * Quando acontecer uma ação de clique, irá limpar um validar agendado
         */
        ADD_MOUSEDOWN_HANDLERS {
            @Override
            String getScript(Component component) {
                return "window.SEH.addMousedownHandlers('"+component.getMarkupId(true)+"');";
            }
        },
        ADD_TEXT_FIELD_HANDLERS {
            @Override
            String getScript(Component component) {
                return "window.SEH.addTextFieldHandlers('"+component.getMarkupId(true)+"');";
            }
        };

        abstract String getScript(Component component);
    }
}
