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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;
import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;

@SuppressWarnings("serial")
public class ListMasterDetailMapper implements IWicketComponentMapper {

    private void checkView(SView view, IModel<SIList<SInstance>> model) {
        if (!(view instanceof SViewListByMasterDetail)) {
            throw new SingularFormException("Error: Mapper " + ListMasterDetailMapper.class.getSimpleName()
                    + " must be associated with a view  of type" + SViewListByMasterDetail.class.getName() + ".", model.getObject());
        }
    }

    @Override
    public void buildView(WicketBuildContext ctx) {

        final IModel<SIList<SInstance>> model = (IModel<SIList<SInstance>>) ctx.getModel();

        checkView(ctx.getView(), model);

        final SViewListByMasterDetail view          = (SViewListByMasterDetail) ctx.getView();
        final ViewMode                viewMode      = ctx.getViewMode();
        final BSContainer<?>          externalAtual = new BSContainer<>("externalContainerAtual");
        final BSContainer<?>          externalIrmao = new BSContainer<>("externalContainerIrmao");

        ctx.getExternalContainer().appendTag("div", true, null, externalAtual);
        ctx.getExternalContainer().appendTag("div", true, null, externalIrmao);

        final MasterDetailModal modal = new MasterDetailModal("mods", model, newItemLabelModel(model), ctx, viewMode, view, externalIrmao);

        externalAtual.appendTag("div", true, null, modal);

        ctx.getContainer().appendTag("div", true, null, new MasterDetailPanel("panel", ctx, model, modal, view));

        modal.add($b.onEnterDelegate(modal.addButton, SINGULAR_PROCESS_EVENT));

    }

    private IModel<String> newItemLabelModel(IModel<SIList<SInstance>> listaModel) {
        //Alteração do model para evitar que haja perda de referencias na renderização das tabelas na tela
        return $m.get(() -> trimToEmpty(listaModel.getObject().asAtr().getItemLabel() != null ? listaModel.getObject().asAtr().getItemLabel() : listaModel.getObject().asAtr().getLabel()));
    }

}
