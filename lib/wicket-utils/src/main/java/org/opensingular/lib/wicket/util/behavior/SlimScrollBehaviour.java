/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;

public class SlimScrollBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format("$('#%s').slimScroll({});", component.getMarkupId(true));
    }

}
