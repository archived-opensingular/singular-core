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
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;

import java.util.Collection;


public class SICompositeTemplateModel extends SInstanceTemplateModel<SIComposite> {

    private final FormObjectWrapper formObjectWrapper;

    public SICompositeTemplateModel(SIComposite composite, FormObjectWrapper formObjectWrapper) {
        super(composite, formObjectWrapper, false);
        this.formObjectWrapper = formObjectWrapper;
    }

    public SICompositeTemplateModel(SIComposite composite, FormObjectWrapper formObjectWrapper, boolean escapeContentHtml) {
        super(composite, formObjectWrapper, escapeContentHtml);
        this.formObjectWrapper = formObjectWrapper;
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        TemplateModel model;
        if (isInvertedPriority()) {
            model = super.get(key);
            if (model == null) {
                model = getTemplateFromField(key);
            }
        } else {
            model = getTemplateFromField(key);
            if (model == null) {
                model = super.get(key);
            }
        }
        return model;
    }

    private TemplateModel getTemplateFromField(String key) {
        return getInstance().getFieldOpt(key).map(instance -> formObjectWrapper.newTemplateModel(instance, escapeContentHtml)).orElse(null);
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return getInstance().isEmptyOfData();
    }

    @Override
    public String getAsString() throws TemplateModelException {
        return StringUtils.defaultString(getInstance().toStringDisplay());
    }

    @Override
    protected Object getValue() {
        return new SInstanceCollectionTemplateModel((Collection<SInstance>) getInstance().getValue(), escapeContentHtml, formObjectWrapper);
    }
}
