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

import java.util.Map;
import java.util.TreeMap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * Behavior to add script that creates a time picker and register the keydown event to hide the timepicker on tab press
 */
public class CreateTimePickerBehavior extends Behavior {

    private TreeMap<String, Object> params = new TreeMap<>();

    public CreateTimePickerBehavior(Map<String, Object> params) {
        this.params.putAll(params);
    }

    public CreateTimePickerBehavior() {
        this.params.put("defaultTime", Boolean.FALSE);
        this.params.put("showMeridian", Boolean.FALSE);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        String markupId = component.getMarkupId(true);
        final String script = String.format("$('#%s').timepicker(%s); ", markupId, getJSONParams())


                + "$('#" + markupId + "').timepicker().on('show.timepicker', function(e) {"
                + " if(e.time.value == '0:00') {"
                + "     $('#" + markupId + "').timepicker('setTime', '00:00 AM');"
                + "  } "
                + "});"
                + "$('#" + markupId + "').on('keydown', "
                + "   function(e){"
                + "     switch (e.keyCode) { "
                + "           case 9: $(this).timepicker('hideWidget'); "
                + "       }"
                + " });"
                + "$('#" + markupId + "').on('remove', "
                + "   function(e){"
                + "       $(this).timepicker('remove'); "
                + " });";

        response.render(OnDomReadyHeaderItem.forScript(script));
    }

    private String getJSONParams() {
        final JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toString();
    }
}
