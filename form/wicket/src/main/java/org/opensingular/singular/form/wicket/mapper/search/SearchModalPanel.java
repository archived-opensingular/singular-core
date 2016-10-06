/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper.search;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.view.SViewSearchModal;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.singular.form.wicket.component.BFModalWindow;
import org.opensingular.singular.form.wicket.model.AbstractSInstanceAwareModel;
import org.opensingular.singular.form.wicket.model.ISInstanceAwareModel;
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

    private final WicketBuildContext            ctx;
    private final ISInstanceAwareModel<String> valueModel;
    private final SViewSearchModal              view;

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