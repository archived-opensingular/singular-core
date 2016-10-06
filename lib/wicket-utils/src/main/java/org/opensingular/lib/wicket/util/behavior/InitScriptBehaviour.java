/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public abstract class InitScriptBehaviour extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(getScript(component)));
    }

    public abstract String getScript(Component component);

}