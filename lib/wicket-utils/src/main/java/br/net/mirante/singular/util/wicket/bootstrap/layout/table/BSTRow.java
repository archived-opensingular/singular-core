/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.bootstrap.layout.table;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSComponentFactory;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;

public class BSTRow extends BSContainer<BSTRow> {

    private BSGridSize defaultGridSize;

    public BSTRow(String id, BSGridSize defaultGridSize) {
        super(id);
        setDefaultGridSize(defaultGridSize);
        setTagName("tr");
    }

    public BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }
    public BSTRow setDefaultGridSize(BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSTRow appendColTag(int colspan, String tag, Component component) {
        return appendColTag(colspan, tag, "", component);
    }

    public BSTRow appendColTag(int colspan, String tag, String attrs, Component component) {
        newCol(colspan).appendTag(tag, true, attrs, component);
        return this;
    }

    public BSTRow appendCol(int colspan, IBSComponentFactory<BSTDataCell> factory) {
        newCol(colspan);
        return this;
    }

    public BSTDataCell newTHeaderCell(IModel<String> model) {
        return newCol(-1).setTagName("th")
            .appendTag("span", new Label("_", model));
    }

    public BSTDataCell newCol(int colspan) {
        return newCol(colspan, BSTDataCell::new);
    }

    public BSTDataCell newCol() {
        return newCol(-1, BSTDataCell::new);
    }

    public <BSC extends BSTDataCell> BSC newCol(int colspan, IBSComponentFactory<BSC> factory) {
        BSC col = super.newComponent(factory);
        col.setTagName("td");
        getDefaultGridSize().col(col, colspan);
        return col;
    }

    public BSControls newFormGroup(int colspan) {
        return newCol(colspan).newFormGroup();
    }

    @Override
    public BSTRow add(Behavior... behaviors) {
        return (BSTRow) super.add(behaviors);
    }
}
