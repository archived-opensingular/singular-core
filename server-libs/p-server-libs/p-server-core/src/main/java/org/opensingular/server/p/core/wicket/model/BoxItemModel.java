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

package org.opensingular.server.p.core.wicket.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opensingular.server.commons.service.dto.BoxItemAction;
import org.opensingular.server.commons.service.dto.ItemAction;

public class BoxItemModel extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;


    public BoxItemModel(Map<String, Object> map) {
        super(map);
    }

    public Long getCod() {
        return ((Integer) get("codPeticao")).longValue();
    }

    public Integer getVersionStamp() {
        Object versionStamp = get("versionStamp");
        if (versionStamp != null) {
            return ((Integer) versionStamp);
        } else {
            return null;
        }
    }

    public Long getProcessInstanceId() {
        Object processInstanceId = get("processInstanceId");
        if (processInstanceId != null) {
            return ((Integer) processInstanceId).longValue();
        } else {
            return null;
        }
    }

    public String getProcessBeginDate() {
        return (String) get("processBeginDate");
    }

    public Map<String, BoxItemAction> getActionsMap() {
        LinkedHashMap actionsMap = new LinkedHashMap<>();

        for (Map<String, Object> map : (List<Map<String, Object>>) get("actions")) {
            final BoxItemAction itemAction = new BoxItemAction(map);
            actionsMap.put(itemAction.getName(), itemAction);
        }
        return actionsMap;
    }

    public BoxItemAction getActionByName(String actionName) {
        return getActionsMap().get(actionName);
    }

    public boolean hasAction(ItemAction itemAction) {
        return getActionsMap().containsKey(itemAction.getName());
    }
}