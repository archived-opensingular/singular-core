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

package org.opensingular.form.wicket.mapper.selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.provider.ProviderLoader;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.form.wicket.model.SelectSInstanceAwareModel;
import org.opensingular.form.wicket.renderer.SingularChoiceRenderer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

import java.io.Serializable;
import java.util.List;

public class SelectMapper extends AbstractControlsFieldComponentMapper {

    private static final long serialVersionUID = 3837032981059048504L;

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();

        final DropDownChoice<Serializable> dropDownChoice = new DropDownChoice<Serializable>(ctx.getCurrentInstance().getName(),
                new SelectSInstanceAwareModel(model),
                getChoicesDetachableModel(model),
                new SingularChoiceRenderer(model)) {
            @Override
            protected String getNullValidDisplayValue() {
                return "Selecione";
            }

            @Override
            protected String getNullKeyDisplayValue() {
                return null;
            }

            @Override
            public boolean isNullValid() {
                return true;
            }
        };
        formGroup.appendSelect(dropDownChoice);
        return dropDownChoice;
    }

    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null && mi.getValue() != null) {
            Serializable instanceObject = mi.getType().asAtrProvider().getConverter().toObject(mi);
            if (instanceObject != null) {
                return mi.getType().asAtrProvider().getDisplayFunction().apply(instanceObject);
            }
        }
        return StringUtils.EMPTY;
    }

    protected LoadableDetachableModel<List<Serializable>> getChoicesDetachableModel(IModel<? extends SInstance> model) {
        return new DefaultOptionsProviderLoadableDetachableModel(model);
    }


    public static class DefaultOptionsProviderLoadableDetachableModel extends LoadableDetachableModel<List<Serializable>> {

        private static final long serialVersionUID = -3852358882003412437L;

        private final IModel<? extends SInstance> model;

        public DefaultOptionsProviderLoadableDetachableModel(IModel<? extends SInstance> model) {
            this.model = model;
        }

        @Override
        protected List<Serializable> load() {
            final RequestCycle requestCycle         = RequestCycle.get();
            boolean            ajaxRequest          = requestCycle != null && requestCycle.find(AjaxRequestTarget.class) != null;
            /* Se for requisição Ajax, limpa o campo caso o valor não for encontrado, caso contrario mantem o valor. */
            boolean            enableDanglingValues = !ajaxRequest;
            return new ProviderLoader(model::getObject, enableDanglingValues).load();
        }
    }

}
