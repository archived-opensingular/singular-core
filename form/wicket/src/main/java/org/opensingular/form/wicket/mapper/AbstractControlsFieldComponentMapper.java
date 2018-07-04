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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.behavior.RequiredBehaviorUtil;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsPanel;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.output.BOutputPanel;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public abstract class AbstractControlsFieldComponentMapper implements IWicketComponentMapper, ISInstanceActionCapable {

    public final static HintKey<Boolean> NO_DECORATION = new HintKey<Boolean>() {
        @Override
        public Boolean getDefaultValue() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isInheritable() {
            return true;
        }
    };

    private final SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);

    protected abstract Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel);

    protected abstract String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model);

    protected Component appendReadOnlyInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final SInstance mi = model.getObject();
        final BOutputPanel comp = new BOutputPanel(mi.getName(), $m.ofValue(getReadOnlyFormattedText(ctx, model)));
        formGroup.appendTag("div", comp);
        return comp;
    }

    @Override
    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final IModel<String> labelModel = new AttributeModel<>(model, SPackageBasic.ATR_LABEL);

        final boolean hintNoDecoration = ctx.getHint(NO_DECORATION);
        final BSContainer<?> container = ctx.getContainer();
        final AttributeModel<String> subtitle = new AttributeModel<>(model, SPackageBasic.ATR_SUBTITLE);
        final ViewMode viewMode = ctx.getViewMode();
        final BSControls formGroup = container.newFormGroup();

        BSLabel label = createLabel(ctx);

        if (hintNoDecoration) {
            formGroup.appendLabel(label);
        } else {
            BSControls labelBar = createLabelBar(label);

            IFunction<AjaxRequestTarget, List<?>> internalContextListProvider = target -> Arrays.asList(
                    AbstractControlsFieldComponentMapper.this,
                    RequestCycle.get().find(AjaxRequestTarget.class),
                    model,
                    model.getObject(),
                    ctx,
                    ctx.getContainer());

            SInstanceActionsPanel.addLeftSecondaryRightPanelsTo(
                    labelBar,
                    instanceActionsProviders,
                    model,
                    false,
                    internalContextListProvider);
            formGroup.appendDiv(labelBar);
        }

        createSubTitle(formGroup, subtitle);

        final Component input;

        if (viewMode.isEdition()) {
            input = appendInput(ctx, formGroup, labelModel);
            formGroup.appendFeedback(ctx.createFeedbackCompactPanel("feedback"));
            formGroup.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    if (model.getObject().getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION) != null) {
                        oldClasses.add("dependant-input-group");
                    }
                    return oldClasses;
                }
            });
            input.add(DisabledClassBehavior.getInstance());

            configureAjaxListeners(ctx, model, label, input);
        } else {
            input = appendReadOnlyInput(ctx, formGroup, labelModel);
        }

        if ((input instanceof LabeledWebMarkupContainer) && (((LabeledWebMarkupContainer) input).getLabel() == null)) {
            ((LabeledWebMarkupContainer) input).setLabel(labelModel);
        }
    }

    /**
     * This method is responsible for configure the events javascript for the input.
     * <p>The Javascripts configuration have to be executed after the onConfigure event,
     * because could be places where the input is added dynamically, so the reload JS have to be executed in the onConfigure. </p>
     * <p> The Javascripts configuration have to be executed in the creation of the input,
     * this happens because have some JS that should be executed before the onConfigure. </p>
     *
     * @param ctx
     * @param model
     * @param label
     * @param input
     * @see org.opensingular.form.wicket.mapper.search.SearchModalMapper
     * inside of
     * @see org.opensingular.form.wicket.mapper.TableListMapper
     * @see org.opensingular.form.wicket.mapper.selection.RadioMapper
     */
    private void configureAjaxListeners(WicketBuildContext ctx, IModel<? extends SInstance> model, BSLabel label, Component input) {
        input.add($b.onConfigure(c -> {
            label.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    return RequiredBehaviorUtil.updateRequiredClasses(oldClasses, model.getObject());
                }
            });
            configureJSForComponent(ctx, input);
        }));
        configureJSForComponent(ctx, input);
    }

    /**
     * Method for reload the configuration of Javascript for the Component.
     * This method will include a meta data for configure the Javascripts elements just one time.
     *
     * @param ctx   The context.
     * @param input The input.
     */
    private void configureJSForComponent(WicketBuildContext ctx, Component input) {
        for (FormComponent<?> fc : findAjaxComponents(input)) {
            if (BooleanUtils.isNotTrue(fc.getMetaData(MDK_COMPONENT_CONFIGURED))) {
                ctx.configure(this, fc);
                fc.setMetaData(MDK_COMPONENT_CONFIGURED, Boolean.TRUE);
            }
        }
    }

    protected FormComponent<?>[] findAjaxComponents(Component input) {
        if (input instanceof FormComponent) {
            return new FormComponent[]{(FormComponent<?>) input};
        } else if (input instanceof MarkupContainer) {
            List<FormComponent<?>> formComponents = new ArrayList<>();
            ((MarkupContainer) input).visitChildren((component, iVisit) -> {
                if (component instanceof FormComponent) {
                    formComponents.add((FormComponent<?>) component);
                    iVisit.dontGoDeeper();
                }
            });
            return formComponents.toArray(new FormComponent[formComponents.size()]);
        } else {
            return new FormComponent[0];
        }

    }

    /**
     * Filtra os eventos (validate e process) garantindo que somente um será disparado
     * <p>
     * Quando algum blur acontecer, verifica se algum change está agendado, caso não esteja, agenda um blur
     * Quando algum change acontecer, verifica se algum blur está agendado, caso tenha limpa o blur dando dando prioridade ao change
     * <p>
     *
     * @param comp o Componente a ser configurado.
     */
    @Override
    public void adjustJSEvents(Component comp) {
        comp.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS));
    }

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.instanceActionsProviders.addSInstanceActionsProvider(sortPosition, provider);
    }
}