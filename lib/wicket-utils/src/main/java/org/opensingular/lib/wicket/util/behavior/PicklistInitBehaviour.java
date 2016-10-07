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

package org.opensingular.lib.wicket.util.behavior;

import java.util.UUID;

import org.apache.wicket.Component;


public class PicklistInitBehaviour extends InitScriptBehaviour {

    private final static String BTN_TEMPLATE = "<button %s type='button' class='btn btn-sm blue-hoki btn-block'><i class='fa %s'></i> %s</button>";
    private final static String BTN_ADD = String.format(BTN_TEMPLATE, "id='%s'", "fa-plus", "Adicionar Todos");
    private final static String BTN_REMOVE = String.format(BTN_TEMPLATE, "id='%s'", "fa-minus", "Remover Todos");

    @Override
    public String getScript(Component component) {

        final String addAllId = UUID.randomUUID().toString();
        final String removeAllId = UUID.randomUUID().toString();

        String jsonConfig = "";
        jsonConfig += "selectableHeader:" + stringfy(String.format(BTN_ADD, addAllId));
        jsonConfig += ",";
        jsonConfig += "selectionHeader:" + stringfy(String.format(BTN_REMOVE, removeAllId));

        final String markupId = component.getMarkupId(true);

        String script = "$('#" + markupId + "').multiSelect({" + jsonConfig + "});";
        script += getOnClickFunction(markupId, addAllId, "select_all");
        script += getOnClickFunction(markupId, removeAllId, "deselect_all");

        return script;
    }

    private String getOnClickFunction(String select, String button, String option) {
        String script = ";";
        script += "$('#{button}').on('click', function(){";
        script += "    $('#{select}').multiSelect('{option}');";
        script += "});";
        return script
                .replace("{button}", button)
                .replace("{select}", select)
                .replace("{option}", option);
    }

    private String stringfy(String string) {
        return "\"" + string + "\"";
    }

}
