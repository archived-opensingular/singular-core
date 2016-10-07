/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket;

import org.opensingular.form.context.SingularFormContextImpl;

public class SingularFormContextWicketImpl extends SingularFormContextImpl implements SingularFormContextWicket {

    private UIBuilderWicket buildContext;

    public SingularFormContextWicketImpl(SingularFormConfigWicketImpl config) {
        super(config);
    }

    @Override
    public UIBuilderWicket getUIBuilder() {
        if (buildContext == null) {
            buildContext = new UIBuilderWicket();
        }
        return buildContext;
    }

}
