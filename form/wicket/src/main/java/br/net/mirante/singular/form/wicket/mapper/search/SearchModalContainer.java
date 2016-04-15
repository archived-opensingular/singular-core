/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.search;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.component.BFModalWindow;
import br.net.mirante.singular.form.wicket.model.BaseIMInstanceAwareModel;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;

class SearchModalContainer extends Panel {

    private final WicketBuildContext            ctx;
    private final IMInstanciaAwareModel<String> valueModel;
    private final SViewSearchModal              view;

    private TextField<String> valueField;
    private BFModalWindow     modal;

    SearchModalContainer(String id, WicketBuildContext ctx) {
        super(id);
        this.ctx = ctx;
        this.view = (SViewSearchModal) ctx.getView();
        this.valueModel = new BaseIMInstanceAwareModel<String>() {
            @Override
            public String getObject() {
                if (!getMInstancia().isEmptyOfData()) {
                    return getMInstancia().toStringDisplay();
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
        add(valueField = buildValueField(), buildModelTrigger());
    }

    private TextField<String> buildValueField() {
        return new TextField<>("valueField", valueModel);
    }

    private void buildAndAppendModalToRootContainer() {
        modal = new BFModalWindow(ctx.getRootContainer().newChildId(), false, false);
        modal.setTitleText(Model.of(Objects.defaultIfNull(view.getTitle(), StringUtils.EMPTY)));
        modal.setBody(new SearchModalContent("selectInputModalContent", ctx, (target) -> {
            modal.hide(target);
            target.add(valueField);
        }));
        ctx.getRootContainer().appendTag("div", modal);
    }

    private Button buildModelTrigger() {
        final Button modalTrigger = new Button("modalTrigger");
        modalTrigger.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                modal.show(target);
            }
        });
        return modalTrigger;
    }

}