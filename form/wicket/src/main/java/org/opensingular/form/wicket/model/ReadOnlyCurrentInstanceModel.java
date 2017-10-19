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

package org.opensingular.form.wicket.model;

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;


public class ReadOnlyCurrentInstanceModel<I extends SInstance> implements IReadOnlyModel<I> {

    private final WicketBuildContext context;

    public ReadOnlyCurrentInstanceModel(WicketBuildContext context) {
        this.context = context;
    }

    @Override
    public I getObject() {
        return context.getCurrentInstance();
    }
}
