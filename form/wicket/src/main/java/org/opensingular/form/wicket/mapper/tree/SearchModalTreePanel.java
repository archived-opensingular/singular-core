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

package org.opensingular.form.wicket.mapper.tree;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;
import org.opensingular.form.view.SViewTree;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.mapper.search.AbstractSearchModalPanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class SearchModalTreePanel extends AbstractSearchModalPanel {

    private BFModalWindow modal;

    public SearchModalTreePanel(String id, WicketBuildContext ctx) {
        super(id, ctx);
    }

    @Override
    protected void buildAndAppendModalToRootContainer() {
        final SViewTree view = ctx.getViewSupplier(SViewTree.class).get();
        modal = new BFModalWindow(ctx.getExternalContainer().newChildId(), false, false);
        modal.setOutputMarkupId(true);
        modal.setTitleText(Model.of(Objects.defaultIfNull(view.getTitle(), StringUtils.EMPTY)));
        SearchModalBodyTreePanel searchModalBody = new SearchModalBodyTreePanel(SELECT_INPUT_MODAL_CONTENT_ID, ctx, this::accept, this::clearInput);
        modal.setBody(searchModalBody).setSize(BSModalBorder.Size.valueOf(view.getModalSize()));
        ctx.getExternalContainer().appendTag("div", modal);
    }

    private void clearInput(AjaxRequestTarget target) {
        getModal().hide(target);
        valueModel.getSInstance().clearInstance();
        target.add(valueField);
        valueField.getBehaviors(AjaxUpdateInputBehavior.class)
                .forEach(ajax -> ajax.onUpdate(target));
    }

    @Override
    protected BFModalWindow getModal() {
        return modal;
    }
}
