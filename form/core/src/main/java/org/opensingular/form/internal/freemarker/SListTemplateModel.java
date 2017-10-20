/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SIList;


public class SListTemplateModel extends SInstanceTemplateModel<SIList<?>> implements TemplateSequenceModel {
    private final FormObjectWrapper formObjectWrapper;

    public SListTemplateModel(SIList<?> list, FormObjectWrapper formObjectWrapper) {
        super(list, formObjectWrapper, false);
        this.formObjectWrapper = formObjectWrapper;
    }

    public SListTemplateModel(SIList<?> list, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(list, formObjectWrapper, escapeContentHtml);
        this.formObjectWrapper = formObjectWrapper;
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        return formObjectWrapper.newTemplateModel(getInstance().get(index), escapeContentHtml);
    }

    @Override
    public int size() throws TemplateModelException {
        return getInstance().size();
    }

    @Override
    public String getAsString() throws TemplateModelException {
        return StringUtils.defaultString(getInstance().toStringDisplay());
    }
}