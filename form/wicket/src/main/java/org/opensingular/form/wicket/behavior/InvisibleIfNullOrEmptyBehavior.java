/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import org.opensingular.singular.util.wicket.model.NullOrEmptyModel;

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
