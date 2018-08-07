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

package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import org.opensingular.form.SInstance;

import java.util.Collection;
import java.util.Iterator;


public class SInstanceCollectionTemplateModel implements TemplateCollectionModel {
    private final Collection<SInstance> collection;
    private final FormObjectWrapper formObjectWrapper;
    private boolean escapeContentHtml;

    public SInstanceCollectionTemplateModel(Collection<SInstance> collection, boolean escapeContentHtml,
                                            FormObjectWrapper formObjectWrapper) {
        this.collection = collection;
        this.escapeContentHtml = escapeContentHtml;
        this.formObjectWrapper = formObjectWrapper;
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        Iterator<SInstance> it = collection.iterator();
        return new TemplateModelIterator() {

            @Override
            public TemplateModel next() {
                return formObjectWrapper.newTemplateModel(it.next(), escapeContentHtml);
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
        };
    }
}