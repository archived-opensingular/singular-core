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