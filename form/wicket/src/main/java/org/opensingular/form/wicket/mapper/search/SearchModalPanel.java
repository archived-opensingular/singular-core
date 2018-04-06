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
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class SearchModalPanel extends AbstractSearchModalPanel {

    private BFModalWindow modal;

    SearchModalPanel(String id, WicketBuildContext ctx) {
        super(id, ctx);
    }

    @Override
    protected void buildAndAppendModalToRootContainer() {
        this.modal = newModalWindow(this.ctx.getViewSupplier(SViewSearchModal.class));
        ctx.getRootContainer().appendTag("div", this.modal);
    }

    private BFModalWindow newModalWindow(ISupplier<SViewSearchModal> viewSupplier) {
        BFModalWindow modal = new BFModalWindow(ctx.getRootContainer().newChildId(), false, false);
        modal.setTitleText(Model.of(Objects.defaultIfNull(viewSupplier.get().getTitle(), StringUtils.EMPTY)));
        modal.setBody(new SearchModalBodyPanel(SELECT_INPUT_MODAL_CONTENT_ID, ctx, (target) -> {
            modal.hide(target);
            target.add(valueField);
            valueField.getBehaviors(AjaxUpdateInputBehavior.class)
                .forEach(ajax -> ajax.onUpdate(target));
        })).setSize(BSModalBorder.Size.valueOf(viewSupplier.get().getModalSize()));
        return modal;
    }

    @Override
    protected BFModalWindow getModal() {
        return modal;
    }
}