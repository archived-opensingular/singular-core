/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;
import static com.google.common.collect.Lists.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

public abstract class BSPanelGrid extends Panel {

    private static final String ID_TAB    = "tab";
    private SingularForm<?>     form      = new SingularForm<>("panel-form");
    private BSGrid              container = new BSGrid("grid");
    private Map<String, BSTab>  tabMap    = new LinkedHashMap<>();
    private BSTab               activeTab = null;

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
        //return new ListView<String>(ID_TAB, tabMap.keySet().stream().collect(Collectors.toList())) {
        //    @Override
        //    protected void populateItem(ListItem<String> item) {

        return new RefreshingView<String>(ID_TAB) {
            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return tabMap.keySet().stream()
                    .map(it -> (IModel<String>) $m.ofValue(it))
                    .iterator();
            }
            @Override
            protected void populateItem(Item<String> item) {
                String id = item.getModelObject();
                final BSTab tab = tabMap.get(id);

                if (activeTab == null && item.getIndex() == 0 || activeTab != null && activeTab.equals(tab)) {
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
                        if (toUpdadeOnTab() != null) {
                            toUpdadeOnTab().forEach((c) -> target.add(c));
                        }
                    }

                };

                link.add(new Label("header-text", tab.getHeaderText()));
                Label label = new Label("header-icon", "");
                label.add(new AttributeModifier("class", tab.iconClass()));
                link.add(label);

                item.add(link);

                onTabCreated(tab, item);
            }
        };
        //        return new ListView<String>(ID_TAB, tabMap.keySet().stream().collect(Collectors.toList())) {
        //            @Override
        //            protected void populateItem(ListItem<String> item) {
        //
        //                String id = item.getModelObject();
        //                final BSTab tab = tabMap.get(id);
        //
        //                if (activeTab == null && item.getIndex() == 0 || activeTab != null && activeTab.equals(tab)) {
        //                    item.add($b.classAppender("active"));
        //                }
        //
        //                item.add($b.attr("data-tab-name", id));
        //
        //                AjaxSubmitLink link = new AjaxSubmitLink("tabAnchor") {
        //                    @Override
        //                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        //                        activeTab = tab;
        //                        buildTabContent();
        //                        updateTab(tab, newArrayList(tabMap.values()));
        //
        //                        target.appendJavaScript("$('.nav-tabs li').removeClass('active');");
        //                        target.appendJavaScript("$('.nav-tabs li[data-tab-name=\"" + id + "\"]').addClass('active');");
        //                        target.add(form);
        //                        if (toUpdadeOnTab() != null) {
        //                            toUpdadeOnTab().forEach((c) -> target.add(c));
        //                        }
        //                    }
        //
        //                };
        //
        //                link.add(new Label("header-text", tab.getHeaderText()));
        //                Label label = new Label("header-icon", "");
        //                label.add(new AttributeModifier("class", tab.iconClass()));
        //                link.add(label);
        //
        //                item.add(link);
        //                
        //                onTabCreated(tab, item);
        //            }
        //        };
    }

    public abstract void updateTab(BSTab tab, List<BSTab> tabs);

    protected void onTabCreated(BSTab tab, Component tabComponent) {}

    public Collection<Component> toUpdadeOnTab() {
        return newArrayList();
    }

    public void buildTabContent() {
        form.remove(container);
        container = new BSGrid("grid");
        form.add(container);

    }

    public BSGrid getContainer() {
        return container;
    }

    public Map<String, BSTab> getTabs() {
        return Collections.unmodifiableMap(tabMap);
    }

    public static final class BSTab implements Serializable {
        private String                               headerText;
        private List<String>                         subtree;
        private String                               iconClass;
        protected IModel<SInstance>                  model;
        private IFunction<IModel<SInstance>, String> iconProcessor;

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
            if (iconClass == null && iconProcessor != null) {
                return iconProcessor.apply(model);
            }
            return iconClass;
        }
        public IModel<SInstance> getModel() {
            return model;
        }
        public SInstance getModelObject() {
            return getModel().getObject();
        }
        public void iconClass(String css) {
            iconClass = css;
        }
        public void iconClass(IFunction<IModel<SInstance>, String> iconProcessor) {
            this.iconProcessor = iconProcessor;
        }
    }
}
