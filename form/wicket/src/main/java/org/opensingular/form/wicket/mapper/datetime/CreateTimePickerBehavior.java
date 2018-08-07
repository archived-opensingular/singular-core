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

package org.opensingular.form.wicket.mapper.datetime;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.wicket.util.behavior.InitScriptBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Behavior to add script that creates a time picker and register the keydown event to hide the timepicker on tab press
 * For more information: <code>https://github.com/jdewit/bootstrap-timepicker</code>
 */
public class CreateTimePickerBehavior extends InitScriptBehaviour {

    private TreeMap<String, Object> params = new TreeMap<>();
    private final PackageTextTemplate initScript = new PackageTextTemplate(CreateTimePickerBehavior.class, "CreateTimePickerBehavior.js");

    public CreateTimePickerBehavior(Map<String, Object> params) {
        this.params.put("defaultTime", Boolean.FALSE);
        this.params.put("showMeridian", Boolean.FALSE);
        if (params != null) {
            this.params.putAll(params);
        }
    }

    @Override
    public String getScript(Component component) {

        String markupId = component.getMarkupId(true);
        Map<String, Object> map = new HashMap<>();
        map.put("timePickerMarkupId", markupId);
        map.put("jsonParams", getJSONParams());
        return initScript.asString(map);

    }

    private String getJSONParams() {
        final JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toString();
    }
}
