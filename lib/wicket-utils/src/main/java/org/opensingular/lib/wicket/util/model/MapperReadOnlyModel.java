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

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.lambda.IFunction;

public class MapperReadOnlyModel<T, U> extends AbstractReadOnlyModel<U> {
    private final IModel<T> rootModel;
    private final IFunction<T, U> mapFunction;

    public MapperReadOnlyModel(IModel<T> rootModel, IFunction<T, U> mapFunction) {
        this.rootModel = rootModel;
        this.mapFunction = mapFunction;
    }

    @Override
    public U getObject() {
        T root = rootModel.getObject();
        return (root == null) ? null : mapFunction.apply(root);
    }

    @Override
    public void detach() {
        rootModel.detach();
    }
}