/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.component;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.modal.BSModalWindow;

public class BFModalWindow extends BSModalWindow {

    public BFModalWindow(String id, boolean wrapBodyWithForm, boolean resetOnBodySwitch) {
        super(id, wrapBodyWithForm, resetOnBodySwitch);
    }
    public BFModalWindow(String id, boolean wrapBodyWithForm) {
        super(id, wrapBodyWithForm);
    }
    public BFModalWindow(String id, IModel<?> model, boolean wrapBodyWithForm, boolean resetOnBodySwitch) {
        super(id, model, wrapBodyWithForm, resetOnBodySwitch);
    }
    public BFModalWindow(String id, IModel<?> model, boolean wrapBodyWithForm) {
        super(id, model, wrapBodyWithForm);
    }
    public BFModalWindow(String id, IModel<?> model) {
        super(id, model);
    }
    public BFModalWindow(String id) {
        super(id);
    }

    @Override
    protected BSModalBorder newModalBorder(String id) {
        return new BFModalBorder(id);
    }
}