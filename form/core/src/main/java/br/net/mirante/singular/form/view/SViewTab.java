/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SViewTab extends SView {

    private STab defaultTab;

    private List<STab> tabs = new ArrayList<>();

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

    public STab addTab(SType<?> type) {
        return addTab(type.getNameSimple(), type.as(AtrBasic.class).getLabel())
            .add(type);
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
