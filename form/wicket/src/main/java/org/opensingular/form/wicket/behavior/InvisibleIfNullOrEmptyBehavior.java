/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import org.opensingular.lib.wicket.util.model.NullOrEmptyModel;

public class InvisibleIfNullOrEmptyBehavior extends Behavior {

    private static final InvisibleIfNullOrEmptyBehavior INSTANCE = new InvisibleIfNullOrEmptyBehavior();
    public static InvisibleIfNullOrEmptyBehavior getInstance() {
        return INSTANCE;
    }

    private InvisibleIfNullOrEmptyBehavior() {}

    @Override
    public void onConfigure(Component component) {
        component.setVisible(!NullOrEmptyModel.nullOrEmpty(component.getDefaultModelObject()));
    }
}
