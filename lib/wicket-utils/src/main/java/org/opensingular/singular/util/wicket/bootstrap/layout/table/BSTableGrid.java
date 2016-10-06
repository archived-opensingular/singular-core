/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.opensingular.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import org.opensingular.singular.commons.lambda.IBiFunction;

public class BSTableGrid extends BSContainer<BSTableGrid> {

    private BSGridSize defaultGridSize = BSGridSize.MD;

    public BSTableGrid(String id) {
        super(id);
    }

    public BSTableGrid(String id, IModel<?> model) {
        super(id, model);
    }

    public BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }
    public BSTableGrid setDefaultGridSize(BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSTRow newRow() {
        return newRow(BSTRow::new);
    }

    public <R extends BSTRow> R newRow(IBiFunction<String, BSGridSize, R> factory) {
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

    public <R extends BSTSection> R newTSection(IBiFunction<String, BSGridSize, R> factory) {
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
