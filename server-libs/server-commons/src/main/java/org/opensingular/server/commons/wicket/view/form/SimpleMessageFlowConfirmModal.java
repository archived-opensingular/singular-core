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

package org.opensingular.server.commons.wicket.view.form;

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.wicket.builder.HTMLParameters;
import org.opensingular.server.commons.wicket.builder.MarkupCreator;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


public class SimpleMessageFlowConfirmModal<T extends PetitionEntity> extends AbstractFlowConfirmModal<T> {

    public SimpleMessageFlowConfirmModal(AbstractFormPage<T> formPage) {
        super(formPage);
    }

    @Override
    public String getMarkup(String idSuffix) {
        return MarkupCreator.div("flow-modal" + idSuffix, new HTMLParameters().styleClass("portlet-body form"), MarkupCreator.div("flow-msg"));
    }

    public BSModalBorder init(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        final BSModalBorder modal = new BSModalBorder("flow-modal" + idSuffix, new StringResourceModel("label.button.confirm", formPage, null));
        addDefaultCancelButton(modal);
        addDefaultConfirmButton(tn, im, vm, modal);
        modal.add(new Label("flow-msg", String.format("Tem certeza que deseja %s ?", tn)));
        return modal;
    }

}