/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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