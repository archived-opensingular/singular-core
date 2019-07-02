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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.list.SViewListByMasterDetail;
import org.opensingular.form.wicket.AbstractCompositeModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;

import javax.annotation.Nullable;

class MasterDetailModal extends AbstractCompositeModal {

    MasterDetailModal(String id,
                      IModel<SIList<SInstance>> model,
                      IModel<String> listLabel,
                      WicketBuildContext ctx,
                      ViewMode viewMode,
                      BSContainer<?> containerExterno) {
        super(id, model, listLabel, ctx, viewMode, containerExterno);

    }

    @Override
    public SViewListByMasterDetail getView() {
        return ctx.getViewSupplier(SViewListByMasterDetail.class).get();
    }

    @Override
    protected WicketBuildContext buildModalContent(BSContainer<?> modalBody, ViewMode viewModeModal) {
        WicketBuildContext context = ctx.createChild(modalBody, containerExterno, currentInstance);

        context.build(viewModeModal);
        return context;
    }

    void showNew(AjaxRequestTarget target) {
        SIList<SInstance> list = getModelObject();
        closeCallback   = target1 -> revert();
        currentInstance = new SInstanceListItemModel<>(getModel(), list.indexOf(list.addNew()));
        actionLabel.setObject(getView().getNewActionLabel());
        configureNewContent(actionLabel.getObject(), target, null);
    }

    @Override
    protected void configureNewContent(String prefix, AjaxRequestTarget target, @Nullable ViewMode viewModeReadOnly) {
        super.configureNewContent(prefix, target, viewModeReadOnly);
        show(target);
    }
}