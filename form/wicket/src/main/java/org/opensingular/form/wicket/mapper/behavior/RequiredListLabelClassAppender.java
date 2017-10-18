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

package org.opensingular.form.wicket.mapper.behavior;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;

import java.util.Set;

public class RequiredListLabelClassAppender extends ClassAttributeModifier {

    private final IModel<? extends SInstance> model;

    public RequiredListLabelClassAppender(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    protected Set<String> update(Set<String> oldClasses) {
        final Boolean required    = model.getObject().getAttributeValue(SPackageBasic.ATR_REQUIRED);
        final Integer minimumSize = model.getObject().getAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE);
        if ((required != null && required) || (minimumSize != null && minimumSize > 0)) {
            oldClasses.add("singular-form-required");
        } else {
            oldClasses.remove("singular-form-required");
        }
        return oldClasses;
    }

}
