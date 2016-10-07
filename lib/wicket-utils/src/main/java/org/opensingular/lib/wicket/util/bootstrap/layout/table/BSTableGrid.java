/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;
import org.opensingular.lib.commons.lambda.IBiFunction;

public class BSTableGrid extends BSContainer<BSTableGrid> {

    private IBSGridCol.BSGridSize defaultGridSize = IBSGridCol.BSGridSize.MD;

    public BSTableGrid(String id) {
        super(id);
    }

    public BSTableGrid(String id, IModel<?> model) {
        super(id, model);
    }

    public IBSGridCol.BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }

    public BSTableGrid setDefaultGridSize(IBSGridCol.BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSTRow newRow() {
        return newRow(BSTRow::new);
    }

    public <R extends BSTRow> R newRow(IBiFunction<String, IBSGridCol.BSGridSize, R> factory) {
        return newComponent(id -> factory.apply(id, getDefaultGridSize()));
    }

    public BSTSection newTBody() {
        return newTSection("tbody");
    }

    public BSTSection newTHead() {
        return newTSection("thead");
    }

    public BSTSection newTFoot() {
        return newTSection("tfoot");
    }

    public BSTSection newTSection(String tagName) {
        return newTSection(BSTSection::new).setTagName(tagName);
    }

    public <R extends BSTSection> R newTSection(IBiFunction<String, IBSGridCol.BSGridSize, R> factory) {
        return newComponent(id -> factory.apply(id, getDefaultGridSize()));
    }

    @Override
    public BSTableGrid add(Behavior... behaviors) {
        return (BSTableGrid) super.add(behaviors);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.setName("table");
    }
}
