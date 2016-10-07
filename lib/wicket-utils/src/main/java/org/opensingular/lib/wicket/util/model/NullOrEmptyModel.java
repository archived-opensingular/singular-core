/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

public class NullOrEmptyModel implements IBooleanModel {

    private final IModel<?> model;

    public NullOrEmptyModel(IModel<?> model) {
        this.model = model;
    }

    @Override
    public Boolean getObject() {
        return nullOrEmpty(model);
    }

    @Override
    public void detach() {
        model.detach();
    }

    public static boolean nullOrEmpty(Object obj) {
        return (obj instanceof String && ((String) obj).trim().isEmpty()) ||
                (obj instanceof IModel<?> && nullOrEmpty(((IModel<?>) obj).getObject())) ||
                (obj instanceof Component && nullOrEmpty(((Component) obj).getDefaultModel())) ||
                (obj == null);
    }
}
