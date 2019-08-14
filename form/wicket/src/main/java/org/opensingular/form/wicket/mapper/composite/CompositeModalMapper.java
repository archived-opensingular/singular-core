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

package org.opensingular.form.wicket.mapper.composite;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstanceViewState;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.calculation.CalculationContext;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.internal.freemarker.FormFreemarkerUtil;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewCompositeModal;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.SingularButton;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.form.wicket.mapper.decorator.SInstanceActionsProviders;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class CompositeModalMapper extends DefaultCompositeMapper {
    private static final MetaDataKey<Integer> CONTAINER_KEY = new MetaDataKey<Integer>() {
        private static final long serialVersionUID = 1L;
    };

    private SInstanceActionsProviders instanceActionsProviders = new SInstanceActionsProviders(this);

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        this.instanceActionsProviders.addSInstanceActionsProvider(sortPosition, provider);
    }

    @Override
    protected ICompositeViewBuilder getViewBuilder(WicketBuildContext ctx) {
        return new CompositeModalMapper.CompositeModalViewBuilder(ctx, this);
    }

    private class CompositeModalViewBuilder extends AbstractCompositeViewBuilder {

        CompositeModalViewBuilder(WicketBuildContext ctx, AbstractCompositeMapper mapper) {
            super(ctx, mapper);
        }

        @Override
        public void buildView() {
            ctx.setHint(AbstractControlsFieldComponentMapper.NO_DECORATION, Boolean.FALSE);
            final IModel<SIComposite> model = (IModel<SIComposite>) ctx.getModel();

            if (!(ctx.getView() instanceof SViewCompositeModal)) {
                throw new SingularFormException("CompositeModalMapper deve ser utilizado com SViewCompositeModal", ctx.getCurrentInstance());
            }
            final SViewCompositeModal view = (SViewCompositeModal) ctx.getView();


            CompositeModal modal = findExistingModal(model);
            if (modal == null) {
                modal = createNewModal(model, view);
            }

            SingularButton button = getButton(view, model, modal);
            Label          label  = getLabel(model);
            TemplatePanel panel = ctx.getContainer().newTemplateTag(t ->
                    "<label wicket:id=\"label\" class=\"control-label composite-modal-label\"></label>" +
                            "<a wicket:id=\"btn\" class=\"btn btn-add\">" +
                            "<wicket:container wicket:id=\"link-label\" />" +
                            "<i wicket:id=\"icon-error\" class=\"fa fa-exclamation-triangle\"></i>" +
                            "<i wicket:id=\"icon-annotation\" ></i>" +
                            "</a>" +
                            "");
            panel.add(getCssResourceBehavior());
            panel.setOutputMarkupId(true);
            panel.add(label);
            panel.add(button);
        }

        private CompositeModal findExistingModal(IModel<SIComposite> model) {
            return ctx.getExternalContainer().visitChildren(CompositeModal.class, new IVisitor<CompositeModal, CompositeModal>() {
                @Override
                public void component(CompositeModal object, IVisit<CompositeModal> visit) {
                    final Integer key = object.getMetaData(CONTAINER_KEY);
                    if (key != null && key.equals(model.getObject().getId())) {
                        visit.stop(object);
                    }
                }
            });
        }

        private CompositeModal createNewModal(IModel<SIComposite> model, SViewCompositeModal view) {
            CompositeModal       modal;
            final BSContainer<?> currentExternal = new BSContainer<>("externalContainerAtual");
            final BSContainer<?> currentSibling  = new BSContainer<>("externalContainerIrmao");

            ctx.getExternalContainer().appendTag("div", true, null, currentExternal);
            ctx.getExternalContainer().appendTag("div", true, null, currentSibling);
            ctx = ctx.createChild(ctx.getContainer(), currentSibling, ctx.getModel());

            modal = new CompositeModal("mods", model, newItemLabelModel(model), ctx, getViewMode(view), ctx.getExternalContainer()) {
                @Override
                protected WicketBuildContext buildModalContent(BSContainer<?> modalBody, ViewMode viewModeModal) {
                    buildFields(ctx, modalBody.newGrid());
                    return ctx;
                }
            };
            modal.add($b.onEnterDelegate(modal.addButton, SINGULAR_PROCESS_EVENT));
            modal.setMetaData(CONTAINER_KEY, model.getObject().getId());

            currentExternal.appendTag("div", true, null, modal);
            return modal;
        }

        public SingularButton getButton(SViewCompositeModal view, IModel<SIComposite> model, CompositeModal modal) {
            SingularButton button = new SingularButton("btn", model) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    modal.show(target);
                }

                @Override
                public boolean isEnabledInHierarchy() {
                    return true;
                }
            };
            IModel<String> labelModel = $m.ofValue((isEdition(view) ? view.getEditActionLabel() : view.getViewActionLabel()) + " " + StringUtils.trimToEmpty(model.getObject().asAtr().getLabel()));
            Label          label      = new Label("link-label", labelModel);
            button.add(label);

            WebMarkupContainer iconError = new WebMarkupContainer("icon-error");
            button.add(iconError);

            SValidationFeedbackHandler feedbackHandler = SValidationFeedbackHandler.bindTo(new FeedbackFence(button))
                    .addInstanceModel(model)
                    .addListener(ISValidationFeedbackHandlerListener.withTarget(t -> t.add(button)));
            button.add($b.classAppender("has-errors", $m.ofValue(feedbackHandler).map(SValidationFeedbackHandler::containsNestedErrors)));
            button.add($b.attr("data-toggle", "tooltip"));
            button.add($b.attr("data-placement", "right"));
            button.add($b.attr("title", $m.get(() -> {
                int qtdErros = feedbackHandler.collectNestedErrors().size();
                if (qtdErros > 0) {
                    return String.format("%s erro(s) encontrado(s)", qtdErros);
                } else {
                    return "";
                }
            })));

            iconError.add($b.visibleIf($m.ofValue(feedbackHandler).map(SValidationFeedbackHandler::containsNestedErrors)));

            WebMarkupContainer iconAnnotation = new WebMarkupContainer("icon-annotation");
            iconAnnotation.add(new AttributeModifier("class", $m.get(() -> new CompositeAnnotationIconState(ctx, model.getObject()).getIconCss())));
            button.add(iconAnnotation);

            return button;
        }

        private boolean isEdition(SViewCompositeModal view) {
            return ctx.getViewMode().isEdition() && view.isEditEnabled()
                    && SInstanceViewState.get(ctx.getCurrentInstance()).isEnabled();
        }

        @Override
        protected void buildFields(WicketBuildContext ctx, BSGrid grid) {
            if (((ctx.getCurrentInstance().getParent() == null) && (!ctx.isNested())) ||
                    ((ctx.getParent().getView() instanceof SViewTab) && !(ctx.getView() instanceof SViewByBlock))) {
                grid.setCssClass("singular-container");
            }
            super.buildFields(ctx, grid);
        }


        private IModel<String> newItemLabelModel(IModel<SIComposite> listModel) {
            //Alteração do model para evitar que haja perda de referencias na renderização das tabelas na tela
            return $m.get(() -> listModel.getObject().asAtr().getLabel());
        }

        protected void buildField(final BSRow row, final SInstanceFieldModel<SInstance> mField) {
            final SViewCompositeModal view   = (SViewCompositeModal) ctx.getView();
            SInstance                 iField = mField.getObject();
            BSCol                     col    = row.newCol();
            configureColspan(ctx, iField, col);
            ctx.createChild(col, ctx.getExternalContainer(), mField).build(getViewMode(view));
        }

        private ViewMode getViewMode(SViewCompositeModal view) {
            return isEdition(view) ? ViewMode.EDIT : ViewMode.READ_ONLY;
        }

        public Label getLabel(IModel<SIComposite> model) {
            SViewCompositeModal viewCompositeModal = (SViewCompositeModal) ctx.getView();
            String              displayString      = viewCompositeModal.getDisplayString();
            return new Label("label", $m.get(() -> {
                if (model.getObject() != null && model.getObject().isNotEmptyOfData()
                        && !displayString.isEmpty()) {
                    CalculationContext calculationContext = new CalculationContext(model.getObject(), model.getObject());
                    return FormFreemarkerUtil.get().createInstanceCalculation(displayString).calculate(calculationContext);
                }
                return "";
            }));
        }

    }

    private Behavior getCssResourceBehavior() {
        return new Behavior() {
            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.render(CssHeaderItem.forReference(new PackageResourceReference(CompositeModalMapper.class, "CompositeModalMapper.css")));
            }
        };
    }

    private static class CompositeAnnotationIconState {
        boolean isAnnotated, hasRejected, hasApproved;
        private       WicketBuildContext ctx;
        private final SIComposite        instance;

        public CompositeAnnotationIconState(WicketBuildContext ctx, SIComposite instance) {
            this.ctx      = ctx;
            this.instance = instance;

            defineState();
        }

        private void defineState() {
            if (ctx.getRootContext().getAnnotationMode().enabled()) {
                checkSubtree();
            }
        }

        private void checkSubtree() {
            AtrAnnotation atrAnnotation = instance.asAtrAnnotation();
            isAnnotated = atrAnnotation.hasAnyAnnotable();
            if (atrAnnotation.hasAnyAnnotationOnTree()) {
                hasRejected = atrAnnotation.hasAnyRefusal();
                hasApproved = !hasRejected;
            }
        }

        private String getIconCss() {
            if (hasRejected) {
                return "annotation-icon annotation-icon-rejected";
            } else if (hasApproved) {
                return "annotation-icon annotation-icon-approved";
            } else if (isAnnotated) {
                return "annotation-icon annotation-icon-empty";
            } else {
                return "";
            }
        }
    }
}