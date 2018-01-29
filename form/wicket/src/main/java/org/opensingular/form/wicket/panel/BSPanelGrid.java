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

package org.opensingular.form.wicket.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.lib.commons.lambda.IBiFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public abstract class BSPanelGrid extends Panel {

    private static final MetaDataKey<BSTab> TAB_KEY    = new MetaDataKey<BSPanelGrid.BSTab>() {};

    private static final String             ID_TAB     = "tab";
    private SingularFormWicket<?>           form       = new SingularFormWicket<>("panel-form");
    private BSTabCol                        navigation = new BSTabCol("tab-navigation");
    private BSTabCol                        content    = new BSTabCol("tab-content");
    private BSGrid                          container  = new BSGrid("grid");
    private Map<String, BSTab>              tabMap     = new LinkedHashMap<>();
    private BSTab                           activeTab  = null;
    private WebMarkupContainer              tabMenu    = new WebMarkupContainer("tab-menu");
    private RefreshingView<String>          tabRepeater;

    public BSPanelGrid(String id) {
        super(id);
    }

    public BSTab addTab(String id, String headerText, List<String> subtree, IModel<SInstance> model, boolean active) {
        BSTab tab = new BSTab(headerText, subtree, model);
        tabMap.put(id, tab);
        if (active) {
            activeTab = tab;
        }
        return tab;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        rebuildForm();
        tabMenu.add(WicketUtils.$b.onReadyScript(c -> "(function(){\n" +
            "  var $tab = $('#" + c.getMarkupId(true) + "'),\n" +
            "    offsetTop = ($tab.offset().top),\n" +
            "    width;\n" +
            "\n" +
            "  function saveTabParentWidth(){\n" +
            "    width = $tab.parent().width();\n" +
            "  }\n" +
            "\n" +
            "  function isBellowPageHeader(){\n" +
            "    return offsetTop - $(window).scrollTop() <= $('.page-header.navbar.navbar-fixed-top').height();\n" +
            "  }\n" +
            "\n" +
            "  saveTabParentWidth();\n" +
            "\n" +
            "  function togglePosition(){\n" +
            "    if(isBellowPageHeader()){\n" +
            "      $tab.css('position', 'fixed');\n" +
            "      $tab.css('top', $('.page-header.navbar.navbar-fixed-top').height() + 25);\n" +
            "      $tab.css('width', width);\n" +
            "    } else{\n" +
            "      $tab.css('position', 'relative');\n" +
            "      $tab.css('top', 'auto');\n" +
            "      $tab.css('width', 'auto');\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  $(window).scroll(togglePosition);\n" +
            "  Wicket.Event.subscribe(\"/ajax/call/complete\", togglePosition);\n" +
            "\n" +
            "  $(window).resize(function(){\n" +
            "    saveTabParentWidth();\n" +
            "    if(isBellowPageHeader()){\n" +
            "      $tab.css('width', width);\n" +
            "    }\n" +
            "  });\n" +
            "}());\n"));
    }

    @SuppressWarnings("unchecked")
    public Item<String> getTabItem(BSTab tab) {
        if (tabRepeater != null) {
            for (Component item : tabRepeater) {
                if (item.getMetaData(TAB_KEY) == tab)
                    return (Item<String>) item;
            }
        }
        return null;
    }

    private void rebuildForm() {
        tabRepeater = buildTabControl(ID_TAB);
        add(form
            .add(navigation
                .add(tabMenu
                    .add(tabRepeater)))
            .add(content));
        buildTabContent();

        configureColspan();
    }

    private RefreshingView<String> buildTabControl(String id) {
        return new RefreshingView<String>(id) {
            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return tabMap.keySet().stream()
                        .filter(this::isAnyChildrenVisible)
                    .map(it -> (IModel<String>) $m.ofValue(it))
                    .iterator();
            }

            private boolean isAnyChildrenVisible(String tabId) {
                BSTab bsTab = tabMap.get(tabId);
                SInstance instance = bsTab.getModelObject();
                if ((instance instanceof SIComposite) && instance.asAtr().exists() && instance.asAtr().isVisible()) {
                    for (String typeName : bsTab.getSubtree()) {
                        SInstance field = instance.getField(typeName);
                        if (field.asAtr().exists() && field.asAtr().isVisible()) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            protected void populateItem(Item<String> item) {
                String id = item.getModelObject();
                final BSTab tab = tabMap.get(id);
                item.setMetaData(TAB_KEY, tab);

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
                label.add(new AttributeModifier("class", $m.get(() -> "tab-header-icon " + tab.iconClass())));
                link.add(label);

                item.add(link);

                onTabCreated(tab, item);
            }
        };
    }
    /**
     * Método responsável por configurar o tamanho da coluna de navegação e de conteúdo
     */
    protected void configureColspan() {
        navigation.xs(3).sm(3).md(3).lg(3);
        content.xs(9).sm(9).md(9).lg(9);
    }

    public abstract void updateTab(BSTab tab, List<BSTab> tabs);

    protected void onTabCreated(BSTab tab, Component tabComponent) {}

    public Collection<Component> toUpdadeOnTab() {
        return newArrayList();
    }

    public void buildTabContent() {
        content.remove(container);
        container = new BSGrid("grid");
        content.add(container);
    }

    public BSGrid getContainer() {
        return container;
    }

    public Map<String, BSTab> getTabs() {
        return Collections.unmodifiableMap(tabMap);
    }

    public BSTabCol getNavigation() {
        return navigation;
    }

    public BSTabCol getContent() {
        return content;
    }

    public static final class BSTab implements Serializable {
        private String                                        headerText;
        private List<String>                                  subtree;
        private String                                        iconClass;
        protected IModel<SInstance>                           model;
        private IBiFunction<BSTab, IModel<SInstance>, String> iconProcessor;

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
                return iconProcessor.apply(this, model);
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
        public void iconClass(IBiFunction<BSTab, IModel<SInstance>, String> iconProcessor) {
            this.iconProcessor = iconProcessor;
        }
    }

    protected static class BSTabCol extends WebMarkupContainer implements IBSGridCol<BSTabCol> {

        public BSTabCol(String id) {
            super(id);
            add(newBSGridColBehavior());
        }

        @Override
        public BSTabCol add(Behavior... behaviors) {
            return (BSTabCol) super.add(behaviors);
        }
    }

}
