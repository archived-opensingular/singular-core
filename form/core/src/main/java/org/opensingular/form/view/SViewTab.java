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

package org.opensingular.form.view;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.lambda.IPredicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * View to customize the render of an {@link SType}
 * With this view is possible to split several groups
 * of the {@link SType}'s fields on diferent tabs for the user.
 *
 * The order of the added tabs is the order that is showed
 * to the user.
 */
public class SViewTab extends SView {

    private STab defaultTab;

    private final List<STab> tabs = new ArrayList<>();

    private Integer navColXs;
    private Integer navColSm;
    private Integer navColMd;
    private Integer navColLg;

    /** {@inheritDoc} */
    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type.isComposite();
    }

    public List<STab> getTabs() {
        return tabs;
    }

    public STab getDefaultTab() {
        return defaultTab;
    }

    /**
     * Add a new tab to the view tab.
     *
     * @param id the id of the tab
     * @param title the title of the tab
     * @return the new tab created
     */
    public STab addTab(String id, String title) {
        STab tab = new STab(this, id, title);
        if (defaultTab == null) {
            defaultTab = tab;
        }

        tabs.add(tab);
        return tab;
    }

    /**
     * Add a new tab to the view tab.
     *
     * @param type the type of the field added to the tab
     * @param title the title of the tab
     * @return the new tab created
     */
    public STab addTab(SType<?> type, String title) {
        return addTab(type.getNameSimple(), title)
            .add(type);
    }

    /**
     * Add a new tab to the view tab.
     * The title of the tab is the same of the {@link SType}
     * @param type the type of the field added to the tab
     * @return the new tab created
     */
    public STab addTab(SType<?> type) {
        return addTab(type, type.asAtr().getLabel());
    }

    /**
     * Configure the column preference size to the navigation bar of the tabs
     * to all device sizes
     */
    public SViewTab navColPreference(Integer value) {
        return navColLg(value).navColMd(value).navColSm(value).navColXs(value);
    }

    /**
     * Configure the column preference size to the navigation bar of the tabs
     * to extra small devices
     */
    public SViewTab navColXs(Integer value) {
        this.navColXs = value;
        return this;
    }

    /**
     * Configure the column preference size to the navigation bar of the tabs
     * to small devices
     */
    public SViewTab navColSm(Integer value) {
        this.navColSm = value;
        return this;
    }

    /**
     * Configure the column preference size to the navigation bar of the tabs
     * to medium devices
     */
    public SViewTab navColMd(Integer value) {
        this.navColMd = value;
        return this;
    }

    /**
     * Configure the column preference size to the navigation bar of the tabs
     * to large devices
     */
    public SViewTab navColLg(Integer value) {
        this.navColLg = value;
        return this;
    }
    
    public Integer getNavColXs() {
        return navColXs;
    }

    public Integer getNavColSm() {
        return navColSm;
    }

    public Integer getNavColMd() {
        return navColMd;
    }

    public Integer getNavColLg() {
        return navColLg;
    }

    /**
     * Each tab defined on {@link SViewTab} is represented by this class.
     *
     */
    public final static class STab implements Serializable {

        private final SViewTab tabView;
        private final String   id;
        private final String   title;
        private final List<String> typesNames;
        private IPredicate<SInstance> visible = IPredicate.all();

        private STab(SViewTab tabView, String id, String title) {
            this.tabView = tabView;
            this.id = id;
            this.title = title;
            typesNames = new ArrayList<>();
        }

        /**
         * Add a type to be rendered by this tab.
         * This method can be called multiple times
         * adding multiple fields.
         *
         * @param field the field to be rendered on this tab
         * @return this
         */
        public STab add(SType<?> field) {
            typesNames.add(field.getNameSimple());
            return this;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getTypesNames() {
            return typesNames;
        }

        public STab setDefault() {
            tabView.defaultTab = this;
            return this;
        }

        /**
         * Used to define if this STab should be visible or not.
         * The default value is to be always visible. You should assume
         * that this will be evaluated only before the form is opened.
         *
         * @param visible code to evaluate if the STab should be visible
         * @return this
         */
        public STab visible(IPredicate<SInstance> visible) {
            this.visible = visible;
            return this;
        }

        public boolean isVisible(SInstance sInstance) {
            return visible.test(sInstance);
        }
    }
}
