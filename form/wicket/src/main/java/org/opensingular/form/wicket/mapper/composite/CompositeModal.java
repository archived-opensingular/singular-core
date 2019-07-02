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

package org.opensingular.form.wicket.mapper.composite;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewCompositeModal;
import org.opensingular.form.wicket.AbstractCompositeModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;

public abstract class CompositeModal extends AbstractCompositeModal {

    public CompositeModal(String id,
                          IModel<? extends SInstance> model,
                          IModel<String> listLabel,
                          WicketBuildContext ctx,
                          ViewMode viewMode,
                          BSContainer<?> containerExterno) {
        super(id, model, listLabel, ctx, viewMode, containerExterno);

    }

    public SViewCompositeModal getView() {
        return ctx.getViewSupplier(SViewCompositeModal.class).get();
    }

    @Override
    public void show(AjaxRequestTarget target) {
        showExisting(target, getModel(), ctx, viewMode);
        super.show(target);
    }
}