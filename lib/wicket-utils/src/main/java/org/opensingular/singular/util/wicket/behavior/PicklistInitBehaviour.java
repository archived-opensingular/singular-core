/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.behavior;

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
