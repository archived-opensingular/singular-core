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

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.component.BFModalWindow;
import org.opensingular.form.wicket.model.AbstractSInstanceAwareModel;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;

public class SearchModalPanel extends Panel {

    public static final String VALUE_FIELD_ID = "valueField";
    public static final String SELECT_INPUT_MODAL_CONTENT_ID = "selectInputModalContent";
    public static final String MODAL_TRIGGER_ID = "modalTrigger";

    private final WicketBuildContext           ctx;
    private final ISInstanceAwareModel<String> valueModel;
    private final SViewSearchModal             view;

    private TextField<String> valueField;
    private BFModalWindow     modal;

    SearchModalPanel(String id, WicketBuildContext ctx) {
        super(id);
        this.ctx = ctx;
        this.view = (SViewSearchModal) ctx.getView();
        this.valueModel = new AbstractSInstanceAwareModel<String>() {
            @Override
            public String getObject() {
                final SInstance mi = getMInstancia();
                if (mi != null && mi.getValue() != null) {
                    if (!mi.isEmptyOfData()) {
                        if (mi.asAtr().getDisplayString() != null) {
                            return mi.toStringDisplay();
                        }
                        if (!(mi instanceof SIComposite)) {
                            return String.valueOf(mi.getValue());
                        }
                        return mi.toString();
                    }
                }
                return null;
            }

            @Override
            public SInstance getMInstancia() {
                return ctx.getModel().getObject();
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        buildAndAppendModalToRootContainer();
        add(valueField = new TextField<>(VALUE_FIELD_ID, valueModel), buildModelTrigger());
    }

    private void buildAndAppendModalToRootContainer() {
        modal = new BFModalWindow(ctx.getRootContainer().newChildId(), false, false);
        modal.setTitleText(Model.of(Objects.defaultIfNull(view.getTitle(), StringUtils.EMPTY)));
        modal.setBody(new SearchModalBodyPanel(SELECT_INPUT_MODAL_CONTENT_ID, ctx, (target) -> {
            modal.hide(target);
            target.add(valueField);
            valueField.getBehaviors(AjaxUpdateInputBehavior.class)
                    .stream()
                    .findFirst().
                    ifPresent(ajax -> ajax.onUpdate(target));
        }));
        ctx.getRootContainer().appendTag("div", modal);
    }

    private Button buildModelTrigger() {
        final Button modalTrigger = new Button(MODAL_TRIGGER_ID);
        modalTrigger.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                modal.show(target);
            }
        });
        return modalTrigger;
    }

}