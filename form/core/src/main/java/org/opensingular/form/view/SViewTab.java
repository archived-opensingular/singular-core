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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;

public class SViewTab extends SView {

    private STab defaultTab;

    private List<STab> tabs = new ArrayList<>();

    private Integer navColXs;
    private Integer navColSm;
    private Integer navColMd;
    private Integer navColLg;
    
    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type instanceof STypeComposite;
    }

    public List<STab> getTabs() {
        return tabs;
    }

    public STab getDefaultTab() {
        return defaultTab;
    }

    public STab addTab(String id, String title) {
        STab tab = new STab(this, id, title);
        if (defaultTab == null) {
            defaultTab = tab;
        }

        tabs.add(tab);
        return tab;
    }

    public STab addTab(SType<?> type, String label) {
        return addTab(type.getNameSimple(), label)
            .add(type);
    }

    public STab addTab(SType<?> type) {
        return addTab(type, type.asAtr().getLabel());
    }
    /**
     * Configura o tamanho geral da coluna de navegação das abas
     * @param valor
     */
    public SViewTab navColPreference(Integer valor) {
        return navColLg(valor).navColMd(valor).navColSm(valor).navColXs(valor);
    }
    /**
     * Configura o tamanho da coluna de navegação das abas em modo Smallest
     * @param valor
     */
    public SViewTab navColXs(Integer valor) {
        this.navColXs = valor;
        return this;
    }
    /**
     * Configura o tamanho da coluna de navegação das abas em modo Small
     * @param valor
     */
    public SViewTab navColSm(Integer valor) {
        this.navColSm = valor;
        return this;
    }
    /**
     * Configura o tamanho da coluna de navegação das abas em modo Medium
     * @param valor
     */
    public SViewTab navColMd(Integer valor) {
        this.navColMd = valor;
        return this;
    }
    /**
     * Configura o tamanho da coluna de navegação das abas em modo Large
     * @param valor
     */
    public SViewTab navColLg(Integer valor) {
        this.navColLg = valor;
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

    public final static class STab implements Serializable {

        private final SViewTab tabView;
        private final String   id;
        private final String   title;
        private final List<String> typesName;

        private STab(SViewTab tabView, String id, String title) {
            this.tabView = tabView;
            this.id = id;
            this.title = title;
            typesName = new ArrayList<>();
        }

        public STab add(SType<?> field) {
            typesName.add(field.getNameSimple());
            return this;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getTypesName() {
            return typesName;
        }

        public STab setDefault() {
            tabView.defaultTab = this;
            return this;
        }
    }
}
