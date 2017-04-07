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

package org.opensingular.lib.wicket.util.model;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

public class NullOrEmptyModel implements IBooleanModel {

    private final IModel<?> model;

    public NullOrEmptyModel(IModel<?> model) {
        this.model = model;
    }

    @Override
    public Boolean getObject() {
        return nullOrEmpty(model);
    }

    @Override
    public void detach() {
        model.detach();
    }

    public static boolean nullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return StringUtils.isBlank((String) obj);
        } else if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map<?, ?>) {
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj instanceof IModel<?>) {
            return nullOrEmpty(((IModel<?>) obj).getObject());
        } else if (obj instanceof Component) {
            return nullOrEmpty(((Component) obj).getDefaultModel());
        }
        return false;
    }
}
