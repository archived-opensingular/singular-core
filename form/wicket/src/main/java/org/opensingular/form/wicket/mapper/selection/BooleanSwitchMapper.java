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

package org.opensingular.form.wicket.mapper.selection;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.view.SViewBooleanSwitch;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.BooleanMapper;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.mapper.behavior.RequiredBehaviorUtil;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public class BooleanSwitchMapper implements IWicketComponentMapper, ISInstanceActionCapable {

    private final SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(WicketBuildContext ctx) {

        final SViewBooleanSwitch<Boolean> view = (SViewBooleanSwitch<Boolean>) ctx.getView();
        final IModel<? extends SInstance> model = ctx.getModel();
        final AttributeModel<String> labelModel = new AttributeModel<>(model, SPackageBasic.ATR_LABEL);

        final BSControls formGroup = ctx.getContainer().newFormGroup();

        final CheckBox input = new CheckBox(model.getObject().getName(), new SInstanceValueModel<>(model));
        final Label label = new Label("_", labelModel);
        adjustJSEvents(ctx, label);

        input.add($b.onReadyScript(c -> JQuery.$(c) + ".bootstrapSwitch()"));

        ctx.configure(this, input);

        label.add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                oldClasses.add("control-label");
                return RequiredBehaviorUtil.updateRequiredClasses(oldClasses, model.getObject());
            }
        });

        Optional<String> onColor = view.getColor(Boolean.TRUE);
        Optional<String> onText = view.getText(Boolean.TRUE);
        Optional<String> offColor = view.getColor(Boolean.FALSE);
        Optional<String> offText = view.getText(Boolean.FALSE);

        formGroup
            .appendLabel(label)
            .appendTag("input", true, ""
                + "type='checkbox' "
                + "class='make-switch' "
                //@formatter:off
                + onColor .map(it ->  "data-on-color='" + it + "' ").orElse("")
                + onText  .map(it ->   "data-on-text='" + it + "' ").orElse("")
                + offColor.map(it -> "data-off-color='" + it + "' ").orElse("")
                + offText .map(it ->  "data-off-text='" + it + "' ").orElse("")
                //@formatter:on
                + "", input)
            .appendFeedback(ctx.createFeedbackCompactPanel("feedback"));

        IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
            BooleanSwitchMapper.this,
            RequestCycle.get().find(AjaxRequestTarget.class),
            model,
            model.getObject(),
            ctx,
            ctx.getContainer());

        SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
            formGroup,
            instanceActionsProviders,
            model,
            false,
            internalContextListProvider);
    }

    @Override
    public void adjustJSEvents(WicketBuildContext ctx, Component comp) {
        comp.add(new SingularEventsHandlers(ADD_TEXT_FIELD_HANDLERS)
            .setOption(SingularEventsHandlers.OPTS_ORIGINAL_VALIDATE_EVENT, "switchChange.bootstrapSwitch"));
    }

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.instanceActionsProviders.addSInstanceActionsProvider(sortPosition, provider);
    }
}