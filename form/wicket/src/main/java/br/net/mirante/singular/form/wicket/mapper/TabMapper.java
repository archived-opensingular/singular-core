/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;
import static com.google.common.collect.Lists.*;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.net.mirante.singular.form.wicket.mapper.composite.DefaultCompositeMapper;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBootstrap;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.view.SViewTab;
import br.net.mirante.singular.form.wicket.ISValidationFeedbackHandlerListener;
import br.net.mirante.singular.form.wicket.SValidationFeedbackHandler;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceFieldModel;
import br.net.mirante.singular.form.wicket.panel.BSPanelGrid;

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
            public Collection<Component> toUpdadeOnTab() {
                if (ctx.getRootContext().getAnnotationMode().enabled()) {
                    return newArrayList(ctx.updateOnRefresh());
                }
                return newArrayList();
            }
            @Override
            protected void onTabCreated(BSTab tab, Component tabComponent) {
                super.onTabCreated(tab, tabComponent);
                ISupplier<List<IModel<? extends SInstance>>> subtreeModels = () -> tab.getSubtree().stream()
                    .map(it -> new SInstanceFieldModel<>(tab.getModel(), it))
                    .collect(toList());
                SValidationFeedbackHandler.bindTo(tabComponent)
                    .addInstanceModels(subtreeModels.get())
                    .addListener(new ISValidationFeedbackHandlerListener() {
                    @Override
                    public void onFeedbackChanged(SValidationFeedbackHandler handler, Optional<AjaxRequestTarget> target, Component container, Collection<SInstance> baseInstances, Collection<IValidationError> oldErrors, Collection<IValidationError> newErrors) {
                        if (target.isPresent())
                            target.get().add(tabComponent);
                    }
                });
                tabComponent.add($b.classAppender("has-errors",
                    $m.get((ISupplier<Boolean>) () -> subtreeModels.get().stream()
                        .map(IModel::getObject)
                        .filter(it -> !SValidationFeedbackHandler.collectNestedErrors(tabComponent).isEmpty())
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
                if (annotatedField.hasAnnotationOnTree()) {
                    checkAnnotation(annotatedField);
                } else if (ctx.getRootContext().getAnnotationMode().editable() &&
                    annotatedField.isOrHasAnnotatedChild()) {
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
