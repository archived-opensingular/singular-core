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

import org.apache.wicket.Component;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Documantion of PickList: https://github.com/lou/multi-select/
 */
public class PicklistInitBehaviour extends InitScriptBehaviour implements Loggable {

    private final static String BTN_TEMPLATE = "<button %s type='button' class='btn btn-sm blue-hoki btn-block'><i class='fa %s'></i> %s</button>";
    private final static String BTN_ADD = String.format(BTN_TEMPLATE, "id='%s'", "fa-plus", "Adicionar Todos");
    private final static String BTN_REMOVE = String.format(BTN_TEMPLATE, "id='%s'", "fa-minus", "Remover Todos");

    @Override
    public String getScript(Component component) {

        final String addAllId = UUID.randomUUID().toString();
        final String removeAllId = UUID.randomUUID().toString();

        try (PackageTextTemplate packageTextTemplate = new PackageTextTemplate(getClass(), "PicklistInitBehaviour.js")) {
            final String markupId = component.getMarkupId(true);
            final Map<String, String> params = new HashMap<>();
            params.put("id", markupId);
            params.put("addAllId", addAllId);
            params.put("buttonAdd", stringfy(String.format(BTN_ADD, addAllId)));
            params.put("buttonRemove", stringfy(String.format(BTN_REMOVE, removeAllId)));
            params.put("removeAllId", removeAllId);
            packageTextTemplate.interpolate(params);
            return packageTextTemplate.asString();
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
            throw SingularException.rethrow(e);
        }

    }


    private String stringfy(String string) {
        return "\"" + string + "\"";
    }

}
