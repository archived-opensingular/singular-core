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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;

public class RangeSliderMapper implements IWicketComponentMapper {

    private final String valorInicialPath, valorFinalPath;

    public RangeSliderMapper(STypeInteger valorInicial, STypeInteger valorFinal) {
        this.valorInicialPath = valorInicial.getNameSimple();
        this.valorFinalPath = valorFinal.getNameSimple();
    }

    @Override
    public void buildView(WicketBuildContext ctx) {

        final BSControls formGroup = createFormGroup(ctx);
        final SIComposite rootInstance = ctx.getCurrentInstance();

        final IModel<? extends SInstance> miInicial = resolveModel(rootInstance, valorInicialPath);
        final IModel<? extends SInstance> miFinal = resolveModel(rootInstance, valorFinalPath);

        final HiddenField valorInicial = new HiddenField<>("valorInicial", miInicial);
        final HiddenField valorFinal = new HiddenField<>("valorFinal", miFinal);

        final Boolean disable = ctx.getViewMode().isVisualization();

        final String initScript = String.format("RangeSliderMapper.init(%s,%s,%s,%s)", formGroup.getMarkupId(true),
                valorInicial.getMarkupId(true), valorFinal.getMarkupId(true), disable);

        formGroup.appendLabel(buildLabel(ctx.getModel()));
        formGroup.appendInputHidden(valorInicial);
        formGroup.appendInputHidden(valorFinal);
        formGroup.add(buildIonRangeScriptBehaviour(initScript));

    }

    private Behavior buildIonRangeScriptBehaviour(String initScript){
        return new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                PackageResourceReference prr = new PackageResourceReference(RangeSliderMapper.class, "RangeSliderMapper.js");
                response.render(JavaScriptHeaderItem.forReference(prr));
                response.render(OnDomReadyHeaderItem.forScript(initScript));
            }
        };
    }

    private BSLabel buildLabel(IModel<? extends SInstance> model) {
        final AttributeModel<String> labelModel = new AttributeModel<>(model, SPackageBasic.ATR_LABEL);
        return new BSLabel("label", labelModel);
    }

    private IModel<? extends SInstance> resolveModel(SIComposite mi, String path) {
        final SInstance SInstance = mi.getField(path);
        final SInstanceRootModel<?> rootModel = new SInstanceRootModel<>(SInstance);
        return new SInstanceValueModel<>(rootModel);
    }

    private BSControls createFormGroup(WicketBuildContext ctx) {
        return ctx.getContainer().newFormGroup();
    }
}
