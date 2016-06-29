/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.module.config;

import br.net.mirante.singular.server.module.wicket.rest.Action;
import br.net.mirante.singular.server.module.wicket.rest.ActionResponse;

public abstract class IController {

    public abstract ActionResponse execute(Action action);

    public boolean isExecutable() {
        return true;
    }
}
