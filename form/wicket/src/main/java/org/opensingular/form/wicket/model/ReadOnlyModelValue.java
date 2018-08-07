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

package org.opensingular.form.wicket.model;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.provider.ProviderLoader;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;

import java.io.Serializable;
import java.util.List;

public class ReadOnlyModelValue implements IReadOnlyModel<List<Serializable>> {

    private IModel<? extends SInstance> model;

    public ReadOnlyModelValue(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    public List<Serializable> getObject() {
        final RequestCycle requestCycle = RequestCycle.get();
        boolean            ajaxRequest  = requestCycle != null && requestCycle.find(AjaxRequestTarget.class) != null;
        /* Se for requisição Ajax, limpa o campo caso o valor não for encontrado, caso contrario mantem o valor. */
        boolean enableDanglingValues = !ajaxRequest;
        return new ProviderLoader(model::getObject, enableDanglingValues).load();
    }
}
