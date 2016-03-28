/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.SInstanceCampoModel;
import br.net.mirante.singular.form.wicket.panel.BSPanelGrid;

public class TabMapper extends DefaultCompostoMapper {

    @Override
    @SuppressWarnings("unchecked")
    public void buildView(final WicketBuildContext ctx) {

        final SIComposite instance = (SIComposite) ctx.getModel().getObject();
        final STypeComposite<SIComposite> tComposto = (STypeComposite<SIComposite>) instance.getType();
        SViewTab tabView = (SViewTab) tComposto.getView();

        BSPanelGrid panel = new BSPanelGrid("panel") {
            @Override
            public void updateTab(BSTab tab, List<BSTab> tabs) {
                renderTab(tab.getSubtree(), this, ctx);
            }

            @Override
            public Collection<Component> toUpdadeOnTab(){
                if(ctx.getRootContext().annotation().enabled()){
                    return newArrayList(ctx.updateOnRefresh());
                }
                return newArrayList();
            }
        };

        for (SViewTab.STab tab : tabView.getTabs()) {
            defineTabIconCss(ctx, instance, tab.getTypesName());
            BSPanelGrid.BSTab t = panel.addTab(tab.getId(), tab.getTitle(), tab.getTypesName(), (IModel<SInstance>) ctx.getModel());
            t.iconClass((Function<IModel<SInstance>, String> & Serializable)
                    (m) -> defineTabIconCss(ctx, (SIComposite) m.getObject(), t.getSubtree()) );
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
        boolean isAnnotated, hasRejected, hasApproved;
        private WicketBuildContext ctx;
        private final SIComposite instance;
        private final List<String> subtree;

        public TabAnnotationIconState(WicketBuildContext ctx, SIComposite instance, List<String> subtree) {
            this.ctx = ctx;
            this.instance = instance;
            this.subtree = subtree;

            defineState();
        }

        private void defineState() {
            if (ctx.getRootContext().annotation().enabled()) {
                subtree.forEach(this::checkSubtree);
            }

        }

        private void checkSubtree(String name) {
            SInstance field = instance.getField(name);
            if (field != null) {
                AtrAnnotation annotatedField = field.asAtrAnnotation();
                if (annotatedField.hasAnnotationOnTree()) {
                    checkAnnotation(annotatedField);
                } else if (ctx.getRootContext().annotation().editable() &&
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
            final SInstanceCampoModel<SInstance> subtree = new SInstanceCampoModel<>(ctx.getModel(), nomeTipo);
            final WicketBuildContext childContext = ctx.createChild(panel.getContainer().newGrid().newColInRow(), true, subtree);
            childContext.init(ctx.getUiBuilderWicket(), ctx.getViewMode());
            childContext.getUiBuilderWicket().build(childContext, childContext.getViewMode());
            childContext.initContainerBehavior();
        }
    }
}
