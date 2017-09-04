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

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.json.JSONObject;

public class SingularEventsHandlers extends Behavior {

    public static final String            OPTS_ORIGINAL_PROCESS_EVENT  = "originalProcessEvent";
    public static final String            OPTS_ORIGINAL_VALIDATE_EVENT = "originalValidateEvent";

    private final FUNCTION[]              functions;
    private final HashMap<String, Object> options                      = new HashMap<>();

    public SingularEventsHandlers(FUNCTION... functions) {
        this.functions = functions;
    }

    public SingularEventsHandlers setOption(String key, Object value) {
        this.options.put(key, value);
        return this;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        response.render(forReference(new PackageResourceReference(SingularEventsHandlers.class, "SingularEventsHandlers.js")));
        Arrays
            .stream(functions)
            .forEach(f -> response.render(OnDomReadyHeaderItem.forScript(f.getScript(component, options))));
    }

    public enum FUNCTION {

        ADD_TEXT_FIELD_HANDLERS {
            @Override
            String getScript(Component component, Map<String, Object> options) {
                JSONObject jsonOpts = new JSONObject();
                options.entrySet().forEach(it -> jsonOpts.put(it.getKey(), it.getValue()));

                return "window.SEH.addTextFieldHandlers('" + component.getMarkupId(true) + "', " + jsonOpts + ");";
            }
        };

        abstract String getScript(Component component, Map<String, Object> options);
    }
}
