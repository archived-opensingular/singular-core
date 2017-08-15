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

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;
import static com.google.common.collect.Lists.newArrayList;

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
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.form.SInstance;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public abstract class BSPanelGrid extends Panel {

    private static final String ID_TAB    = "tab";
    private SingularFormWicket<?> form       = new SingularFormWicket<>("panel-form");
    private BSTabCol              navigation = new BSTabCol("tab-navigation");
    private BSTabCol              content    = new BSTabCol("tab-content");
    private BSGrid                container  = new BSGrid("grid");
    private Map<String, BSTab>    tabMap     = new LinkedHashMap<>();
    private BSTab                 activeTab  = null;
    private WebMarkupContainer    tabMenu    = new WebMarkupContainer("tab-menu");

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
        tabMenu.add(WicketUtils.$b.onReadyScript(c -> {
            return "(function(){\n" +
                    "  var $tab = $('#"+c.getMarkupId(true)+"'),\n" +
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
                    "  $(window).scroll(function(){\n" +
                    "    if(isBellowPageHeader()){\n" +
                    "      $tab.css('position', 'fixed');\n" +
                    "      $tab.css('top', $('.page-header.navbar.navbar-fixed-top').height() + 15);\n" +
                    "      $tab.css('width', width);\n" +
                    "    } else{\n" +
                    "      $tab.css('position', 'relative');\n" +
                    "      $tab.css('top', 'auto');\n" +
                    "      $tab.css('width', 'auto');\n" +
                    "    }\n" +
                    "  });\n" +
                    "\n" +
                    "  $(window).resize(function(){\n" +
                    "    saveTabParentWidth();\n" +
                    "    if(isBellowPageHeader()){\n" +
                    "      $tab.css('width', width);\n" +
                    "    } \n" +
                    "  });\n" +
                    "}());";
        }));
    }


    private void rebuildForm() {
        add(form);
        form.add(navigation);
        form.add(content);
        navigation.add(tabMenu.add(buildTabControl()));
        buildTabContent();

        configureColspan();
    }

    private Component buildTabControl() {
        return new RefreshingView<String>(ID_TAB) {
            @Override
            protected Iterator<IModel<String>> getItemModels() {
                return tabMap.keySet().stream()
                        .map(it -> (IModel<String>) $m.ofValue(it))
                        .iterator();
            }

            @Override
            protected void populateItem(Item<String> item) {
                String      id  = item.getModelObject();
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
                label.add(new AttributeModifier("class", "tab-header-icon " + tab.iconClass()));
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
