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

import static org.apache.commons.lang3.StringUtils.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.decorator.action.SIcon;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.DisabledClassBehavior;
import org.opensingular.form.wicket.behavior.InvisibleIfNullOrEmptyBehavior;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.mapper.decorator.AbstractSIconActionDelegate;
import org.opensingular.form.wicket.model.AttributeModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.output.BOutputPanel;

public abstract class AbstractControlsFieldComponentMapper implements IWicketComponentMapper, ISInstanceActionCapable {

    final static HintKey<Boolean>     NO_DECORATION = (HintKey<Boolean>) () -> Boolean.FALSE;

    private ISInstanceActionsProvider instanceActionsProvider;

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
        final BSLabel label = new BSLabel("label", labelModel);
        final BSControls formGroup = container.newFormGroup();

        configureLabel(ctx, labelModel, hintNoDecoration, label);

        Iterator<SInstanceAction> actionsIterator = actionIterator(model);
        if (actionsIterator.hasNext()) {
            buildActions(ctx, model, label, formGroup, actionsIterator);
        } else {
            formGroup.appendLabel(label);
        }

        formGroup.newHelpBlock(subtitle)
            .add($b.classAppender("hidden-xs"))
            .add($b.classAppender("hidden-sm"))
            .add($b.classAppender("hidden-md"))
            .add(InvisibleIfNullOrEmptyBehavior.getInstance());

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
            input.add($b.onConfigure(c -> label.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> oldClasses) {
                    if (model.getObject().getAttributeValue(SPackageBasic.ATR_REQUIRED)) {
                        oldClasses.add("singular-form-required");
                    } else {
                        oldClasses.remove("singular-form-required");
                    }
                    return oldClasses;
                }
            })));
            for (FormComponent<?> fc : findAjaxComponents(input)) {
                ctx.configure(this, fc);
            }
        } else {
            input = appendReadOnlyInput(ctx, formGroup, labelModel);
        }

        if ((input instanceof LabeledWebMarkupContainer) && (((LabeledWebMarkupContainer) input).getLabel() == null)) {
            ((LabeledWebMarkupContainer) input).setLabel(labelModel);
        }
    }

    private void buildActions(WicketBuildContext ctx, final IModel<? extends SInstance> model, final BSLabel label, final BSControls formGroup, Iterator<SInstanceAction> actionsIterator) {
        BSContainer<?> actionsContainer = new BSContainer<>("actions");
        formGroup
            .appendDiv(new BSControls("labelBar")
                .appendLabel(label)
                .appendDiv(actionsContainer
                    .add($b.classAppender("actions"))))
            .add(new Behavior() {
                @Override
                public void renderHead(Component component, IHeaderResponse response) {
                    response.render(CssHeaderItem.forCSS(""
                        + "\n .form-group>div>.actions {"
                        + "\n   float:right;"
                        + "\n   display: inline-block;"
                        + "\n   padding: 0 0 2px;"
                        + "\n   zoom: 70%;"
                        + "\n }"
                        + "\n .form-group>div>.actions>a.btn>i {"
                        + "\n   font-size: 1.6em;"
                        + "\n   padding: 2px 0 0 1px;"
                        + "\n }"
                        + "\n ",
                        AbstractControlsFieldComponentMapper.class.getName() + "_style"));
                }
            });
        while (actionsIterator.hasNext()) {
            SInstanceAction action = actionsIterator.next();
            SIcon icon = action.getIcon();
            String text = action.getText();
            String desc = action.getDescription();

            BSContainer<?> link = new BSContainer<>("btn");
            if (icon != null)
                link.appendTag("i", new WebMarkupContainer("icon").add($b.classAppender(icon.getCssClass())))
                    .add($b.classAppender("btn-icon-only", $m.get(() -> isBlank(text))));

            if (isNotBlank(text))
                link.appendTag("span", new Label("text", text));

            if (isNotBlank(desc))
                link.add($b.attr("title", desc));

            link.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    List<?> contextList = Arrays.asList(
                        AbstractControlsFieldComponentMapper.this,
                        target,
                        model,
                        model.getObject(),
                        ctx,
                        ctx.getContainer());

                    SInstanceAction.Delegate delegate = new AbstractSIconActionDelegate(model::getObject, contextList);
                    action.getActionHandler().onAction(model::getObject, delegate);
                }
            });

            actionsContainer.appendTag("a", true, "class='btn btn-circle btn-default'", link);
        }
    }

    private Iterator<SInstanceAction> actionIterator(final IModel<? extends SInstance> model) {
        if (instanceActionsProvider != null) {
            Iterable<SInstanceAction> actions = instanceActionsProvider.getActions(this, model.getObject());
            return actions.iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

    protected void configureLabel(WicketBuildContext ctx, IModel<String> labelModel, boolean hintNoDecoration, BSLabel label) {
        label.add(DisabledClassBehavior.getInstance());
        label.setVisible(!hintNoDecoration);
        label.add($b.onConfigure(c -> {
            if (ctx.isTitleInBlock()) {
                c.setVisible(false);
            } else if (StringUtils.isEmpty(labelModel.getObject())) {
                c.setVisible(false);
            }
        }));
    }

    protected FormComponent<?>[] findAjaxComponents(Component input) {
        if (input instanceof FormComponent) {
            return new FormComponent[] { (FormComponent<?>) input };
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
     * @param comp o Componente a ser configurado.
     */
    @Override
    public void adjustJSEvents(Component comp) {
        comp.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS));
    }

    @Override
    public void setSInstanceActionsProvider(ISInstanceActionsProvider provider) {
        this.instanceActionsProvider = provider;
    }
}