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

package org.opensingular.server.commons.service.dto;

import static org.opensingular.server.commons.flow.rest.DefaultServerREST.EXECUTE;
import static org.opensingular.server.commons.flow.rest.DefaultServerREST.PATH_BOX_ACTION;

import java.io.Serializable;
import java.util.Map;

import org.opensingular.server.commons.form.FormActions;

public class BoxItemAction implements Serializable {

    private String name;
    private String endpoint;
    private FormActions formAction;
    private boolean useExecute = false;

    public BoxItemAction() {
    }

    public BoxItemAction(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.endpoint = (String) map.get("endpoint");
        this.useExecute = (Boolean) map.get("useExecute");
    }

    public static BoxItemAction newExecuteInstante(Object id, String actionName) {
        String endpointUrl = PATH_BOX_ACTION + EXECUTE + "?id=" + id;

        final BoxItemAction boxItemAction = new BoxItemAction();
        boxItemAction.setName(actionName);
        boxItemAction.setEndpoint(endpointUrl);
        boxItemAction.setUseExecute(true);
        return boxItemAction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isUseExecute() {
        return useExecute;
    }

    public void setUseExecute(boolean useExecute) {
        this.useExecute = useExecute;
    }

    public FormActions getFormAction() {
        return formAction;
    }

    public void setFormAction(FormActions formAction) {
        this.formAction = formAction;
    }
}