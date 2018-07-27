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

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.opensingular.lib.wicket.util.util.WicketUtils.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.AtrBootstrap;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.ISValidationFeedbackHandlerListener;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.SValidationFeedbackHandler;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.form.wicket.panel.BSPanelGrid;
import org.opensingular.form.wicket.panel.BSPanelGrid.BSTab;
import org.opensingular.form.wicket.util.SingularFormProcessingPayload;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.scripts.Scripts;

public class TabMapper implements IWicketComponentMapper {

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(final WicketBuildContext ctx) {
        final ISupplier<SViewTab> tabViewSupplier = () -> (SViewTab) ctx.getView();

        BSPanelGrid panel = newGrid(ctx);

        if (ctx.getCurrentInstance().getParent() == null) {
            panel.add(new ClassAttributeModifier() {
                @Override
                protected Set<String> update(Set<String> set) {
                    set.add("singular-container-tab");
                    return set;
                }
            });
        }

        final SIComposite instance = (SIComposite) ctx.getModel().getObject();

        final SViewTab.STab tabDefault = tabViewSupplier.get().getDefaultTab();

        for (SViewTab.STab tab : tabViewSupplier.get().getTabs()) {
            if (tab.isVisible(instance)) {
                defineTabIconCss(ctx, instance, tab.getTypesNames());
                IModel<SInstance> baseInstanceModel = (IModel<SInstance>) ctx.getModel();
                BSPanelGrid.BSTab t = panel.addTab(tab.getId(), tab.getTitle(), tab.getTypesNames(), baseInstanceModel, tab.isDefault());
                t.iconClass((t1, m) -> defineTabIconCss(ctx, (SIComposite) m.getObject(), t1.getSubtree()));
            }
        }

        final IModel<String> label = $m.ofValue(trimToEmpty(instance.asAtr().getLabel()));
        if (isNotBlank(label.getObject())) {
            ctx.getContainer().newTag("h4", new Label("_title", label));
        }

        ctx.getContainer().newTag("div", panel);

        renderTab(tabDefault.getTypesNames(), panel, ctx);

        ctx.getContainer().add(new Behavior() {
            @Override
            public void onEvent(Component component, IEvent<?> event) {
                super.onEvent(component, event);

                final AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
                Object payload = event.getPayload();
                if (payload instanceof SInstance) {
                    final SInstance instance = (SInstance) payload;
                    for (BSTab tab : panel.getTabs().values()) {
                        if (instance.isDescendantOf(tab.getModelObject())) {
                            panel.getTabItem(tab).ifPresent(target::add);
                        }
                    }
                } else if (payload instanceof SingularFormProcessingPayload) {
                    SingularFormProcessingPayload singularPayload = (SingularFormProcessingPayload) payload;
                    Set<String> typeNames = tabViewSupplier.get().getTabs().stream()
                        .flatMap(it -> it.getTypesNames().stream())
                        .collect(Collectors.toSet());

                    if (singularPayload.hasUpdatedType(typeNames)) {
                        target.add(panel);
                    }
                }
            }
        });
    }

    @Nonnull
    protected BSPanelGrid newGrid(WicketBuildContext ctx) {
        return new BSPanelGrid("panel") {

            @Override
            protected void onInitialize() {
                super.onInitialize();
                getNavigation().add($b.attr("style", "padding-right:0px;"));
            }

            @Override
            public void updateTab(BSTab tab, List<BSTab> tabs) {
                renderTab(tab.getSubtree(), this, ctx);
                AjaxRequestTarget ajaxRequestTarget = RequestCycle.get().find(AjaxRequestTarget.class);
                ctx.updateExternalContainer(ajaxRequestTarget);
                ajaxRequestTarget.appendJavaScript(Scripts.scrollToTop());
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
                        .anyMatch(it -> !SValidationFeedbackHandler.collectNestedErrors(new FeedbackFence(tabComponent)).isEmpty()))));
            }

            @Override
            protected void configureColspan() {
                super.configureColspan();
                // Configura o tamanho da aba de acordo com os atributos bootstrap informados
                SIComposite instance = (SIComposite) ctx.getModel().getObject();
                SViewTab tabView = (SViewTab) ctx.getView();
                AtrBootstrap bootstrap = instance.asAtrBootstrap();
                // da prioridade ao que foi definido na View e nos atributos em seguida
                configureBSColumns(tabView, bootstrap);
            }

            private void configureBSColumns(SViewTab tabView, AtrBootstrap bootstrap) {
                Integer colPreference = bootstrap.getColPreference();
                Integer colXs = resolveCol(tabView.getNavColXs(), bootstrap.getColXs(colPreference), BSPanelGrid.BSTabCol.MAX_COLS);
                Integer colSm = resolveCol(tabView.getNavColSm(), bootstrap.getColSm(colPreference), BSPanelGrid.BSTabCol.MAX_COLS);
                Integer colMd = resolveCol(tabView.getNavColMd(), bootstrap.getColMd(colPreference), BSPanelGrid.BSTabCol.MAX_COLS);
                Integer colLg = resolveCol(tabView.getNavColLg(), bootstrap.getColLg(colPreference), BSPanelGrid.BSTabCol.MAX_COLS);

                BSTabCol content = getContent();
                configureContentColuns(colXs, colSm, colMd, colLg, content);
            }

            private void configureContentColuns(Integer colXs, Integer colSm, Integer colMd, Integer colLg, BSTabCol content) {
                if (colXs != null) {
                    getNavigation().xs(colXs);
                    content.xs(BSTabCol.MAX_COLS - colXs);
                }
                if (colSm != null) {
                    getNavigation().sm(colSm);
                    content.sm(BSTabCol.MAX_COLS - colSm);
                }
                if (colMd != null) {
                    getNavigation().md(colMd);
                    content.md(BSTabCol.MAX_COLS - colMd);
                }
                if (colLg != null) {
                    getNavigation().lg(colLg);
                    content.lg(BSTabCol.MAX_COLS - colLg);
                }
            }

        };
    }

    private Integer resolveCol(Integer cols, Integer defaultCols, int max) {
        Integer c = defaultIfNull(cols, defaultCols);
        return ((c != null) && (c < max)) ? c : null;
    }

    public String defineTabIconCss(WicketBuildContext ctx, SIComposite instance, List<String> subtree) {
        return new TabAnnotationIconState(ctx, instance, subtree).getIconCss();
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

    private void renderTab(List<String> typeNames, BSPanelGrid panel, WicketBuildContext ctx) {
        for (String typeName : typeNames) {
            SInstanceFieldModel<SInstance> subtree = new SInstanceFieldModel<>(ctx.getModel(), typeName);
            WicketBuildContext childContext = ctx.createChild(panel.getContainer().newGrid().newColInRow(), ctx.getExternalContainer(), subtree);
            childContext.init(ctx.getViewMode());
            childContext.build();
            childContext.initContainerBehavior();
        }
    }
}
