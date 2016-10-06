/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.custom;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceFieldModel;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;
import org.opensingular.singular.form.showcase.component.Resource;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;

/**
 * Custom String Mapper
 */
@CaseItem(componentName = "Custom Mapper", subCaseName = "Tabbed Panel", group = Group.CUSTOM, resources = {
    @Resource(value = CaseCustomTabbedPanelMapperPackage.CustomAjaxTabbedPanel.class, extension = "html")
})
public class CaseCustomTabbedPanelMapperPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<SIComposite> form = pb.createCompositeType("testForm");

        STypeComposite<SIComposite> tab1 = form.addFieldComposite("tab1");
        tab1.asAtr().label("Aba 1");
        tab1.addFieldString("texto1").asAtr().label("Texto 1");
        tab1.addFieldString("texto2").asAtr().label("Texto 2");

        STypeComposite<SIComposite> tab2 = form.addFieldComposite("tab2");
        tab2.asAtr().label("Aba 2");
        tab2.addFieldBoolean("bool1").asAtr().label("Boolean 1");
        tab2.addFieldBoolean("bool2").asAtr().label("Boolean 2");

        STypeComposite<SIComposite> tab3 = form.addFieldComposite("tab3");
        tab3.asAtr().label("Aba 3");
        tab3.addFieldInteger("int1").asAtr().label("Inteiro 1");
        tab3.addFieldInteger("int2").asAtr().label("Inteiro 2");

        //@destacar
        form.withCustomMapper(() -> new CustomTabMapper());
    }

    // Mapper recursivo
    static final class CustomTabMapper implements IWicketComponentMapper {

        @Override
        @SuppressWarnings("unchecked")
        public void buildView(WicketBuildContext ctx) {
            final IModel<? extends SInstance> model = ctx.getModel();
            final STypeComposite<SIComposite> stype = (STypeComposite<SIComposite>) model.getObject().getType();

            final BSContainer<?> container = ctx.getContainer();
            final List<CustomTab> tabs = new ArrayList<>();
            for (SType<?> field : stype.getFields()) {
                tabs.add(new CustomTab(ctx, new SInstanceFieldModel<>(model, field.getNameSimple())));
            }
            container.appendComponent(id -> new CustomAjaxTabbedPanel(id, tabs));
        }
    }

    // Implementação de Tab que cria o contexto e o container de cada filho, e chama o builder para construí-los.
    static final class CustomTab implements ITab {
        private final SInstanceFieldModel<SInstance> model;
        private final WicketBuildContext             parentCtx;
        public CustomTab(WicketBuildContext parentCtx, SInstanceFieldModel<SInstance> model) {
            this.parentCtx = parentCtx;
            this.model = model;
        }
        @Override
        public IModel<String> getTitle() {
            return IReadOnlyModel.of(() -> model.getObject().asAtr().getLabel());
        }
        @Override
        public WebMarkupContainer getPanel(String containerId) {
            // container filho
            final BSContainer<?> childContainer = new BSContainer<>(containerId);
            // contexto filho
            final WicketBuildContext childCtx = parentCtx.createChild(childContainer, true, model);

            // chamada recursiva ao UIBuilderWicket
            childCtx.getUiBuilderWicket().build(childCtx, childCtx.getViewMode());

            return childContainer;
        }
        @Override
        public boolean isVisible() {
            return true;
        }
    }

    // Utilizando AjaxTabbedPanel built-in no wicket-extensions, customizado para funcionar com o bootstrap. 
    static final class CustomAjaxTabbedPanel extends AjaxTabbedPanel<CustomTab> {
        private CustomAjaxTabbedPanel(String id, List<CustomTab> tabs) {
            super(id, tabs);
        }
        @Override
        protected String getSelectedTabCssClass() {
            return "active";
        }
        @Override
        protected String getTabContainerCssClass() {
            return "nav nav-tabs";
        }
    }
}
