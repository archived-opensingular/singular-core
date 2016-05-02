/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;
import static com.google.common.collect.Lists.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

public abstract class BSPanelGrid extends Panel {

    private SingularForm<?> form = new SingularForm<>("panel-form");
    private BSGrid container = new BSGrid("grid");
    private Map<String, BSTab> tabMap = new LinkedHashMap<>();
    private BSTab activeTab = null;

    public BSPanelGrid(String id) {
        super(id);
    }

    public BSTab addTab(String id, String headerText, List<String> subtree, IModel<SInstance> model) {
        BSTab tab = new BSTab(headerText, subtree, model);
        tabMap.put(id, tab);
        return tab;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        rebuildForm();
    }

    private void rebuildForm() {
        add(form.add(buildTabControl()));
        buildTabContent();
    }

    private Component buildTabControl() {

        return new ListView<String>("tab", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<String> item) {

                String id = item.getModelObject();
                final BSTab tab = tabMap.get(id);

                if(activeTab == null && item.getIndex() == 0 || activeTab != null && activeTab.equals(tab)){
                    item.add($b.classAppender("active"));
                }

                item.add($b.attr("data-tab-name", id));

                AjaxSubmitLink link = new AjaxSubmitLink("tabAnchor") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        activeTab = tab;
                        buildTabContent();
                        updateTab(tab, newArrayList(tabMap.values()));

                        target.appendJavaScript("$('.nav-tabs li').removeClass('active');");
                        target.appendJavaScript("$('.nav-tabs li[data-tab-name=\"" + id + "\"]').addClass('active');");
                        target.add(form);
                        if(toUpdadeOnTab() != null){
                            toUpdadeOnTab().forEach((c) -> target.add(c) );
                        }
                    }

                };

                link.add(new Label("header-text", tab.getHeaderText()));
                Label label = new Label("header-icon", "");
                label.add(new AttributeModifier("class",tab.iconClass()));
                link.add(label);

                item.add(link);
            }
        };
    }

    public abstract void updateTab(BSTab tab, List<BSTab> tabs);

    public Collection<Component> toUpdadeOnTab(){   return newArrayList(); }

    public void buildTabContent() {
        form.remove(container);
        container = new BSGrid("grid");
        form.add(container);

    }

    public BSGrid getContainer() {
        return container;
    }

    public static final class BSTab implements Serializable {
        private String headerText;
        private List<String> subtree;
        private String iconClass;
        protected IModel<SInstance> model;
        private Function<IModel<SInstance>, String> iconProcessor;

        public BSTab(String headerText, List<String> subtree, IModel<SInstance> model) {
            this.headerText = headerText;
            this.subtree = subtree;
            this.model = model;
        }

        public String getHeaderText() {
            return headerText;
        }

        public List<String> getSubtree() {
            return subtree;
        }

        public String iconClass() {
            if(iconClass == null && iconProcessor != null){
                return iconProcessor.apply(model);
            }
            return iconClass;
        }
        public void iconClass(String css) { iconClass = css; }
        public void iconClass(Function<IModel<SInstance>, String> iconProcessor) {
            this.iconProcessor = (Function<IModel<SInstance>, String> & Serializable) iconProcessor;
        }
    }
}
