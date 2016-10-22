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


import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.service.IFormService;
import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.template.Template;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import javax.inject.Named;

public class ReadOnlyFormPage extends Template {

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    @Inject
    private IFormService formService;

    private final IModel<Long>    formVersionEntityPK;
    private final IModel<Boolean> showAnnotations;

    public ReadOnlyFormPage(IModel<Long> formVersionEntityPK, IModel<Boolean> showAnnotations) {
        this.formVersionEntityPK = formVersionEntityPK;
        this.showAnnotations = showAnnotations;
    }

    @Override
    protected Content getContent(String id) {
        return new ReadOnlyFormContent(id, formVersionEntityPK, formService, singularFormConfig, showAnnotations);
    }

    @Override
    protected boolean withMenu() {
        return false;
    }
}