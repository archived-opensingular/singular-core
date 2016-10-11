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

package org.opensingular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.wicket.WicketBuildContext;
import org.slf4j.Logger;

import org.opensingular.form.SInstance;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

public class ReadOnlyControlsFieldComponentMapper extends AbstractControlsFieldComponentMapper {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ReadOnlyControlsFieldComponentMapper.class);

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();

        final FormComponent<?> field = new TextField<>(model.getObject().getName(), new Model<String>() {
            @Override
            public String getObject() {
                return getReadOnlyFormattedText(ctx, model);
            }

            @Override
            public void setObject(String object) {}
        });

        field.setEnabled(false);
        field.setLabel(labelModel);
        formGroup.appendInputText(field);

        return field;

    }

    @Override
    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        final String displayString = model.getObject().toStringDisplay();
        if (displayString == null) {
            LOGGER.warn("A avaliação de toStringDisplay retornou null");
        }
        return displayString;
    }
}
