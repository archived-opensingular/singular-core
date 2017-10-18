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

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public abstract class SInstanceTemplateModel<INSTANCE extends SInstance> implements TemplateScalarModel, TemplateHashModel {
    private final INSTANCE instance;
    private final FormObjectWrapper formObjectWrapper;
    private boolean invertedPriority;
    protected boolean escapeContentHtml;

    public SInstanceTemplateModel(INSTANCE instance, FormObjectWrapper formObjectWrapper) {
        this.instance = instance;
        this.formObjectWrapper = formObjectWrapper;
    }

    public SInstanceTemplateModel(INSTANCE instance, FormObjectWrapper formObjectWrapper, boolean esccapeContentHtml) {
        this.instance = instance;
        this.formObjectWrapper = formObjectWrapper;
        escapeContentHtml = esccapeContentHtml;
    }

    protected INSTANCE getInstance() {
        return instance;
    }

    protected boolean isInvertedPriority() {
        return invertedPriority;
    }

    protected Object getValue() {
        return instance.getValue();
    }

    @Override
    public TemplateModel get(String key) throws TemplateModelException {
        if ("toStringDisplayDefault".equals(key)) {
            return new SInstanceZeroArgumentMethodTemplate<>(getInstance(), key, SInstance::toStringDisplayDefault);
        } else if ("value".equals(key) || "getValue".equals(key)) {
            return new SInstanceZeroArgumentMethodTemplate<>(getInstance(), key, i -> getValue());
        } else if ("_inst".equals(key)) {
            Optional<Constructor<?>> constructor = Arrays.stream(getClass().getConstructors())
                    .filter(c -> c.getParameterCount() == 3 && c.getParameterTypes()[0].isAssignableFrom(getInstance().getClass()))
                    .findFirst();
            if (!constructor.isPresent()) {
                throw new SingularFormException(
                        "Não foi encontrado o construtor " + getClass().getSimpleName() + "(SInstance)");
            }
            SInstanceTemplateModel<INSTANCE> newSelf;
            try {
                newSelf = (SInstanceTemplateModel<INSTANCE>) constructor.get().newInstance(getInstance(), formObjectWrapper, escapeContentHtml);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new SingularFormException("Erro instanciado _inst", e);
            }
            newSelf.invertedPriority = true;
            return newSelf;
        } else if ("toStringDisplay".equals(key)) {
            return new SInstanceZeroArgumentMethodTemplate<>(getInstance(), key, SInstance::toStringDisplay);
        }
        return null;
    }

    @Override
    public boolean isEmpty() throws TemplateModelException {
        return false;
    }
}
