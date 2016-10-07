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
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.server.commons.wicket.view.template.Content;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


public class ReadOnlyFormContent extends Content {

    private final IModel<Long>        formVersionEntityPK;
    private final IFormService        formService;
    private final SFormConfig<String> formConfig;

    private SingularFormPanel<String> singularFormPanel;

    public ReadOnlyFormContent(String id, IModel<Long> formVersionEntityPK, IFormService formService, SFormConfig<String> formConfig) {
        super(id);
        this.formVersionEntityPK = formVersionEntityPK;
        this.formService = formService;
        this.formConfig = formConfig;
        build();
    }

    private void build() {

        final FormVersionEntity formVersionEntity = formService.loadFormVersionEntity(formVersionEntityPK.getObject());
        final FormKey           formKey           = formService.keyFromObject(formVersionEntity.getFormEntity().getCod());

        final RefType refType = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return formConfig.getTypeLoader().loadTypeOrException(formVersionEntity.getFormEntity().getFormType().getAbbreviation());
            }
        };

        add(new Form("form").add(singularFormPanel = new SingularFormPanel<String>("singularFormPanel", formConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                return formService.loadSInstance(formKey, refType, singularFormConfig.getDocumentFactory(), formVersionEntityPK.getObject());
            }
        }));

        singularFormPanel.setViewMode(ViewMode.READ_ONLY);
    }


    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of("");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of("");
    }

}