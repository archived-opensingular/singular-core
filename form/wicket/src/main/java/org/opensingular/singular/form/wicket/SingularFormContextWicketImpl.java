/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket;

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
