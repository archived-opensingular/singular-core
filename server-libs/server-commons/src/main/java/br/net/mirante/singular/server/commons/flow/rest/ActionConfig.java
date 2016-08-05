/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.net.mirante.singular.flow.core.property.MetaDataRef;
import br.net.mirante.singular.server.commons.util.ServerActionConstants;

/**
 * Classe responsável por guardar a configuração de ações
 * disponível para o módulo
 */
public class ActionConfig {

    public static final MetaDataRef<ActionConfig> KEY = new MetaDataRef<>(ActionConfig.class.getName(), ActionConfig.class);

    private static final Map<String, Class<? extends IController>> MAP_DEFAULT_ACTIONS;

    private List<String> defaultActions;

    private Map<String, Class<? extends IController>> customActions;

    static {
        MAP_DEFAULT_ACTIONS = new HashMap<>();
        MAP_DEFAULT_ACTIONS.put(ServerActionConstants.ACTION_RELOCATE, AtribuirController.class);
    }

    public ActionConfig() {
        defaultActions = new ArrayList<>();
        customActions = new HashMap<>();
    }

    public Map<String, Class<? extends IController>> getCustomActions() {
        return Collections.unmodifiableMap(customActions);
    }

    public ActionConfig addAction(String name, Class<? extends IController> controllerClass) {
        customActions.put(name, controllerClass);
        return this;
    }

    public List<String> getDefaultActions() {
        return Collections.unmodifiableList(defaultActions);
    }

    public ActionConfig addDefaultAction(String action) {
        defaultActions.add(action);
        return this;
    }

    public Class<? extends IController> getAction(String name) {
        Class<? extends IController> controllerClass = customActions.get(name);

        if (controllerClass == null) {
            controllerClass = MAP_DEFAULT_ACTIONS.get(name);
        }

        return controllerClass;
    }

    public boolean containsAction(String name) {
        return customActions.containsKey(name)
                || defaultActions.contains(name);
    }
}
