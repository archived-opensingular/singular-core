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

package org.opensingular.form.wicket.mapper.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

public class SearchModalMapper extends AbstractControlsFieldComponentMapper {

    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final SView view = ctx.getView();

        if (view instanceof SViewSearchModal) {
            final SearchModalPanel selectModalBusca = new SearchModalPanel("SelectModalBusca", ctx);
            formGroup.appendDiv(selectModalBusca);
            return selectModalBusca;
        }
        throw new RuntimeException("SearchModalMapper only works with a MSelecaoPorModalBuscaView.");
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null && mi.getValue() != null) {
            if (mi.asAtr().getDisplayString() != null) {
                return mi.toStringDisplay();
            }
            if (!(mi instanceof SIComposite)) {
                return String.valueOf(mi.getValue());
            }
            return mi.toString();
        }
        return StringUtils.EMPTY;
    }

}
