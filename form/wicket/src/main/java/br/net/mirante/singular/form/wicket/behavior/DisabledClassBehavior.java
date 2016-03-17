/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;

public class DisabledClassBehavior extends AttributeAppender {

    private static final DisabledClassBehavior INSTANCE = new DisabledClassBehavior();
    public static DisabledClassBehavior getInstance() {
        return INSTANCE;
    }

    private DisabledClassBehavior() {
        super("class", "disabled", " ");
    }
    public boolean isEnabled(Component component) {
        return !component.isEnabledInHierarchy();
    }
}
