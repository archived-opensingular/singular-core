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

package org.opensingular.lib.wicket.util.button;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe DropdownButtonPanel
 * <p>
 * Constroi um dropdown do boostrap com os links informados
 */
public class DropDownButtonPanel extends Panel {

    //Componentes do Wicket
    private Button                   dropdownButton      = new Button("dropdown-button");
    private Label                    dropdownButtonLabel = new Label("dropdown-label");
    private WebMarkupContainer       dropdownMenu        = new WebMarkupContainer("dropdown-menu");
    private ListView<ButtonMetadata> buttons             = new ListView<ButtonMetadata>("buttons") {
        @Override
        protected void populateItem(ListItem<ButtonMetadata> item) {
            populateButton(item);
        }
    };

    //Models
    private IModel<String>                    dropdownLabel;
    private IModel<ArrayList<ButtonMetadata>> buttonsMetadata;
    private IModel<Boolean>                   invisibleIfEmpty;
    private IModel<Boolean>                   pullRight;

    /**
     * Construtor principal
     *
     * @param id o wicket id
     */
    public DropDownButtonPanel(String id) {
        super(id);
        buttonsMetadata = new Model<>(new ArrayList<>());
        invisibleIfEmpty = Model.of(Boolean.FALSE);
        pullRight = Model.of(Boolean.FALSE);
    }

    public DropDownButtonPanel setDropdownLabel(IModel<String> dropdownLabel) {
        this.dropdownLabel = dropdownLabel;
        return this;
    }

    public DropDownButtonPanel setInvisibleIfEmpty(Boolean invisibleIfEmpty) {
        this.invisibleIfEmpty.setObject(invisibleIfEmpty);
        return this;
    }

    public DropDownButtonPanel setPullRight(Boolean pullRight) {
        this.pullRight.setObject(pullRight);
        return this;
    }

    /**
     * Adiciona o botão ao dropdown
     *
     * @param label         o label do botao
     * @param buttonFactory o construtor do botao
     * @return o dropdownpanel atual
     */
    public DropDownButtonPanel addButton(IModel<String> label, IFunction<String, Button> buttonFactory) {
        buttonsMetadata.getObject().add(new ButtonMetadata(label, buttonFactory));
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(dropdownButton.add(dropdownButtonLabel.setDefaultModel(dropdownLabel)));
        add(dropdownMenu.add(buttons));
        buttons.setList(buttonsMetadata.getObject());
        if (pullRight.getObject()) {
            dropdownMenu.add(Shortcuts.$b.attrAppender("class", "pull-right", " "));
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (invisibleIfEmpty.getObject()) {
            setVisible(!buttonsMetadata.getObject().isEmpty());
        }
    }

    /**
     * Popula cada botão
     *
     * @param item o container do botao atual
     */
    private void populateButton(ListItem<ButtonMetadata> item) {
        ButtonMetadata buttonMetadata = item.getModelObject();
        item.add(buttonMetadata.buttonFactory.apply("button").add(new Label("button-label", buttonMetadata.label)));
    }

    /**
     * Metadados para construção dos botoes
     */
    private static class ButtonMetadata implements Serializable {

        private IModel<String>            label;
        private IFunction<String, Button> buttonFactory;

        private ButtonMetadata(IModel<String> label, IFunction<String, Button> buttonFactory) {
            this.label = label;
            this.buttonFactory = buttonFactory;
        }
    }

}