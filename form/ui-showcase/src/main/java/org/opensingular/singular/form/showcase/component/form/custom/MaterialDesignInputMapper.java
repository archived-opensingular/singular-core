/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.custom;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.output.BOutputPanel;

public class MaterialDesignInputMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final SInstance mi = ctx.getCurrentInstance();
        final BSLabel label = new BSLabel("label", new AttributeModel<>(model, SPackageBasic.ATR_LABEL));

        if(ctx.getViewMode().isVisualization()){
            formGroup.appendLabel(label);
            formGroup.appendTag("div", new BOutputPanel(mi.getName(), getOutputString(mi)));
        } else {
            formGroup.appendInputText(new TextField<>(mi.getName(), new SInstanceValueModel<>(model)));
            formGroup.appendLabel(label);
            formGroup.add(new AttributeAppender("class", " form-md-line-input form-md-floating-label"));
        }
    }

    private IModel<String> getOutputString(SInstance mi) {
        if (mi.getValue() != null) {
            return Model.of(String.valueOf(mi.getValue()));
        } else {
            return Model.of(StringUtils.EMPTY);
        }
    }

}