/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.output;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.bootstrap.layout.BSWellBorder;


public class BOutputPanel extends Panel {

    /**
     *
     */
    private static final long serialVersionUID = -7120790446032810735L;

    private final IModel<String> outputText;

    private Label outputTextLabel;

    public BOutputPanel(String id, IModel<String> outputText) {
        super(id);
        this.outputText = outputText;
        this.outputTextLabel = new Label("output", outputText);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(BSWellBorder.small("well").add(outputTextLabel));
    }

    public Label getOutputTextLabel() {
        return outputTextLabel;
    }

}