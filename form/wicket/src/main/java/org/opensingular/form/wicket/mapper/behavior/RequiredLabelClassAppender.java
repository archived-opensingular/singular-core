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

package org.opensingular.form.wicket.mapper.behavior;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Class responsible for include the Required class CSS.
 */
public class RequiredLabelClassAppender extends ClassAttributeModifier {

    private final IModel<? extends SInstance> model;
    private List<String> classesCss;

    public RequiredLabelClassAppender(IModel<? extends SInstance> model) {
        this.model = model;
    }

    /**
     * @param model   The model.
     * @param classes The classes Css to be included in the update model.
     */
    public RequiredLabelClassAppender(IModel<? extends SInstance> model, String... classes) {
        this(model);
        classesCss = Arrays.asList(classes);

    }

    @Override
    protected Set<String> update(Set<String> oldClasses) {
        if (CollectionUtils.isNotEmpty(classesCss)) {
            oldClasses.addAll(classesCss);
        }
        return RequiredBehaviorUtil.updateRequiredClasses(oldClasses, model.getObject());
    }

}
