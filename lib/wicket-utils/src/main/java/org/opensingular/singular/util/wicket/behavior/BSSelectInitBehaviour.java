/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.behavior;

import org.apache.wicket.Component;


public class BSSelectInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format("$('#%s').selectpicker()", component.getMarkupId(true));
    }

}
