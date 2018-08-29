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

package org.opensingular.form.wicket.mapper;

import org.apache.commons.lang3.StringUtils;
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
import org.opensingular.form.view.SViewCheckBox;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.mapper.behavior.RequiredLabelClassAppender;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;

import java.util.Arrays;
import java.util.List;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS;

public class BooleanMapper implements IWicketComponentMapper, ISInstanceActionCapable {

    private final SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);

    @Override
    public void buildView(WicketBuildContext ctx) {
        switch (ctx.getViewMode()) {
            case READ_ONLY:
                buildForVisualization(ctx);
                break;
            case EDIT:
                buildForEdition(ctx);
                break;
        }
    }

    protected void buildForEdition(WicketBuildContext ctx) {
        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final IModel<? extends SInstance> model = ctx.getModel();
        final CheckBox input = new CheckBox(model.getObject().getName(), new SInstanceValueModel<>(model));
        configureCheckBoxWithLabelByView(ctx, formGroup, input);

        final BSContainer<?> checkboxDiv = input.getMetaData(BSControls.CHECKBOX_DIV);
        if (checkboxDiv != null) { //
            IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
                    BooleanMapper.this,
                    RequestCycle.get().find(AjaxRequestTarget.class),
                    model,
                    model.getObject(),
                    ctx,
                    ctx.getContainer());

            SInstanceActionsPanel.addPrimarySecondaryPanelsTo(
                    checkboxDiv,
                    instanceActionsProviders,
                    model,
                    false,
                    internalContextListProvider);
        }

        input.add(DisabledClassBehavior.getInstance());
        formGroup.appendFeedback(ctx.createFeedbackCompactPanel("feedback"));
        ctx.configure(this, input);

    }

    /**
     * Configure the checkBox Input and the Label according with View.
     *
     * @param ctx       Ctx containing the View.
     * @param formGroup The formGroup to add the checkBox.
     * @param input     The CheckBox Input that should be add in the FormGroup.
     */
    private void configureCheckBoxWithLabelByView(WicketBuildContext ctx, BSControls formGroup,
            CheckBox input) {
        final IModel<? extends SInstance> model = ctx.getModel();
        Label label;
        if (viewIsConfigToChangeAlignmentLabel(ctx)) {
            label = createLabel(ctx);
            BSControls labelBar = createLabelBar(label);
            formGroup.appendLabel(labelBar);

            final AttributeModel<String> subtitle = new AttributeModel<>(model, SPackageBasic.ATR_SUBTITLE);
            createSubTitle(formGroup, subtitle);
            formGroup.appendCheckboxWithoutLabel(input, ((SViewCheckBox) ctx.getView()).getAlignmentOfLabel());

        } else {
            final AttributeModel<String> labelModel = new AttributeModel<>(model, SPackageBasic.ATR_LABEL);
            label = buildLabel("_", labelModel);
            formGroup.appendCheckbox(input, label, null);
        }

        adjustJSEvents(ctx, label);
        label.add(new RequiredLabelClassAppender(model));
    }

    /**
     * Method to verify is the Label of checkBox will be above of the checkbox.
     *
     * @param ctx The context.
     * @return True if the label will be show above.
     */
    private boolean viewIsConfigToChangeAlignmentLabel(WicketBuildContext ctx) {
        return ctx.getView() instanceof SViewCheckBox && ((SViewCheckBox) ctx.getView()).getAlignmentOfLabel() != null;
    }

    protected void buildForVisualization(WicketBuildContext ctx) {
        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final IModel<? extends SInstance> model = ctx.getModel();


        if (viewIsConfigToChangeAlignmentLabel(ctx)) {
            formGroup.appendLabel(createLabel(ctx));

            final AttributeModel<String> subtitle = new AttributeModel<>(model, SPackageBasic.ATR_SUBTITLE);
            createSubTitle(formGroup, subtitle);

            createTagForViewCheckBox(formGroup, ctx, false);

        } else {
            createTagForViewCheckBox(formGroup, ctx, true);
        }

    }

    @SuppressWarnings("squid:S1854")
    private void createTagForViewCheckBox(BSControls formGroup, WicketBuildContext ctx, boolean showLabelInline) {
        final IModel<? extends SInstance> model = ctx.getModel();
        String idSuffix = (model != null && model.getObject() != null) ? model.getObject().getName() : StringUtils.EMPTY;
        formGroup.appendComponent((IBSComponentFactory<Component>) componentId ->
                new CheckBoxPanel("checkBox" + idSuffix, ctx, showLabelInline));
    }

    /**
     * This method is responsible for create the label of checkBox.
     * Be careful, this should be LABEL, can't be BSLabel because the changed of the behavior.
     *
     * @param id         The id of the label.
     * @param labelModel The model of the label.
     * @return Retuns label.
     */
    protected Label buildLabel(String id, AttributeModel<String> labelModel) {
        return (Label) new Label(id, labelModel.getObject())
                .setEscapeModelStrings(false);
    }

    @Override
    public void adjustJSEvents(WicketBuildContext ctx, Component comp) {
        comp.add(new SingularEventsHandlers(ADD_TEXT_FIELD_HANDLERS));
    }

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.instanceActionsProviders.addSInstanceActionsProvider(sortPosition, provider);
    }
}