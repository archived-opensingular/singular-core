/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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