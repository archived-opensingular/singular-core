/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.service.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoxItemActions extends LinkedHashMap<String, BoxItemAction> implements  Serializable {

    public BoxItemActions() {
    }

    public BoxItemActions(Map<? extends String, ? extends BoxItemAction> map) {
        super(map);
    }

    public BoxItemActions add(BoxItemAction action) {
        put(action.getName(), action);
        return this;
    }

    public void addAll(BoxItemAction... actions) {
        for (BoxItemAction action : actions) {
            put(action.getName(), action);
        }
    }



}
