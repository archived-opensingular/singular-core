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

import static org.opensingular.lib.wicket.util.util.WicketUtils.*;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.mapper.composite.DefaultCompositeMapper;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.basic.AtrBootstrap;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.panel.BSPanelGrid;

public class TabMapper extends DefaultCompositeMapper {

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(final WicketBuildContext ctx) {

        final STypeComposite<SIComposite> tComposto = (STypeComposite<SIComposite>) ctx.getModel().getObject().getType();
        SViewTab tabView = (SViewTab) tComposto.getView();

        BSPanelGrid panel = new BSPanelGrid("panel") {

            @Override
            protected void onInitialize() {
                super.onInitialize();
                getNavigation().add($b.attr("style", "padding-right:0px;"));
            }

            @Override
            public void updateTab(BSTab tab, List<BSTab> tabs) {
                renderTab(tab.getSubtree(), this, ctx);
            }

            @Override
            protected void onTabCreated(BSTab tab, Component tabComponent) {
                super.onTabCreated(tab, tabComponent);
                ISupplier<List<IModel<? extends SInstance>>> subtreeModels = () -> tab.getSubtree().stream()
                    .map(it -> new SInstanceFieldModel<>(tab.getModel(), it))
                    .collect(toList());
                SValidationFeedbackHandler.bindTo(new FeedbackFence(tabComponent))
                    .addInstanceModels(subtreeModels.get())
                    .addListener((ISValidationFeedbackHandlerListener) (handler, target, container, baseInstances, oldErrors, newErrors) -> {
                        if (target != null) {
                            target.add(tabComponent);
                        }
                    });
                tabComponent.add($b.classAppender("has-errors",
                    $m.get((ISupplier<Boolean>) () -> subtreeModels.get().stream()
                        .map(IModel::getObject)
                        .filter(it -> !SValidationFeedbackHandler.collectNestedErrors(new FeedbackFence(tabComponent)).isEmpty())
                        .findAny()
                        .isPresent())));
            }
            @Override
            protected void configureColspan() {
                super.configureColspan();
                // Configura o tamanho da aba de acordo com os atributos bootstrap informados
                SIComposite instance = (SIComposite) ctx.getModel().getObject();
                SViewTab tabView = (SViewTab) instance.getType().getView();
                AtrBootstrap bootstrap = instance.asAtrBootstrap();
                // da prioridade ao que foi definido na View e nos atributos em seguida
                final Optional<Integer> colXs = Optional.ofNullable(Optional.ofNullable(tabView.getNavColXs()).orElse(bootstrap.getColXs(bootstrap.getColPreference())));
                final Optional<Integer> colSm = Optional.ofNullable(Optional.ofNullable(tabView.getNavColSm()).orElse(bootstrap.getColSm(bootstrap.getColPreference())));
                final Optional<Integer> colMd = Optional.ofNullable(Optional.ofNullable(tabView.getNavColMd()).orElse(bootstrap.getColMd(bootstrap.getColPreference())));
                final Optional<Integer> colLg = Optional.ofNullable(Optional.ofNullable(tabView.getNavColLg()).orElse(bootstrap.getColLg(bootstrap.getColPreference())));
                
                if(colXs.filter(x -> x < BSTabCol.MAX_COLS ).isPresent()){
                    getNavigation().xs(colXs.get());
                    getContent().xs(BSTabCol.MAX_COLS - colXs.get());
                }
                if(colSm.filter(x -> x < BSTabCol.MAX_COLS ).isPresent()){
                    getNavigation().sm(colSm.get());
                    getContent().sm(BSTabCol.MAX_COLS - colSm.get());
                }
                if(colMd.filter(x -> x < BSTabCol.MAX_COLS ).isPresent()){
                    getNavigation().md(colMd.get());
                    getContent().md(BSTabCol.MAX_COLS - colMd.get());
                }
                if(colLg.filter(x -> x < BSTabCol.MAX_COLS ).isPresent()){
                    getNavigation().lg(colLg.get());
                    getContent().lg(BSTabCol.MAX_COLS - colLg.get());
                }
            }
        };
        
        if (ctx.getCurrentInstance().getParent() == null) {
            panel.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> set) {
                    set.add("singular-container-tab");
                    return set;
                }
            });
        }

        SIComposite instance = (SIComposite) ctx.getModel().getObject();
        for (SViewTab.STab tab : tabView.getTabs()) {
            defineTabIconCss(ctx, instance, tab.getTypesName());
            IModel<SInstance> baseInstanceModel = (IModel<SInstance>) ctx.getModel();
            BSPanelGrid.BSTab t = panel.addTab(tab.getId(), tab.getTitle(), tab.getTypesName(), baseInstanceModel);
            t.iconClass((m) -> defineTabIconCss(ctx, (SIComposite) m.getObject(), t.getSubtree()));
        }

        final IModel<String> label = $m.ofValue(trimToEmpty(instance.asAtr().getLabel()));
        if (isNotBlank(label.getObject())) {
            ctx.getContainer().newTag("h4", new Label("_title", label));
        }

        ctx.getContainer().newTag("div", panel);

        SViewTab.STab tabDefault = tabView.getDefaultTab();

        renderTab(tabDefault.getTypesName(), panel, ctx);

    }

    public String defineTabIconCss(WicketBuildContext ctx, SIComposite instance,
                                   List<String> subtree) {
        return new TabAnnotationIconState(ctx, instance, subtree)
            .getIconCss();

    }

    private static class TabAnnotationIconState {
        boolean                    isAnnotated, hasRejected, hasApproved;
        private WicketBuildContext ctx;
        private final SIComposite  instance;
        private final List<String> subtree;

        public TabAnnotationIconState(WicketBuildContext ctx, SIComposite instance, List<String> subtree) {
            this.ctx = ctx;
            this.instance = instance;
            this.subtree = subtree;

            defineState();
        }

        private void defineState() {
            if (ctx.getRootContext().getAnnotationMode().enabled()) {
                subtree.forEach(this::checkSubtree);
            }

        }

        private void checkSubtree(String name) {
            SInstance field = instance.getField(name);
            if (field != null) {
                AtrAnnotation annotatedField = field.asAtrAnnotation();
                if (annotatedField.hasAnyAnnotationOnTree()) {
                    checkAnnotation(annotatedField);
                } else if (ctx.getRootContext().getAnnotationMode().editable() &&
                    annotatedField.hasAnyAnnotable()) {
                    isAnnotated = true;
                }
            }
        }

        private void checkAnnotation(AtrAnnotation annotatedField) {
            if (annotatedField.hasAnyRefusal()) {
                hasRejected = true;
            } else {
                hasApproved = true;
            }
        }

        private String getIconCss() {
            if (hasRejected) {
                return "fa fa-comment sannotation-color-danger";
            } else if (hasApproved) {
                return "fa fa-comment sannotation-color-info";
            } else if (isAnnotated) {
                return "fa fa-comment-o";
            } else {
                return "";
            }
        }
    }

    private void renderTab(List<String> nomesTipo, BSPanelGrid panel, WicketBuildContext ctx) {
        for (String nomeTipo : nomesTipo) {
            final SInstanceFieldModel<SInstance> subtree = new SInstanceFieldModel<>(ctx.getModel(), nomeTipo);
            final WicketBuildContext childContext = ctx.createChild(panel.getContainer().newGrid().newColInRow(), true, subtree);
            childContext.init(ctx.getUiBuilderWicket(), ctx.getViewMode());
            childContext.getUiBuilderWicket().build(childContext, childContext.getViewMode());
            childContext.initContainerBehavior();
        }
    }
}
