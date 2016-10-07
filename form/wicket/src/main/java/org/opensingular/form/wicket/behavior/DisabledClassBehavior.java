/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.behavior;

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
