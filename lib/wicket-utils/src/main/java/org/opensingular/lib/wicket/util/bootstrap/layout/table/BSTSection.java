/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout.table;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;
import org.opensingular.lib.commons.lambda.IBiFunction;

public class BSTSection extends BSContainer<BSTSection> {

    private IBSGridCol.BSGridSize defaultGridSize = IBSGridCol.BSGridSize.MD;

    public BSTSection(String id) {
        super(id);
    }

    public BSTSection(String id, IBSGridCol.BSGridSize defaultGridSize) {
        super(id);
        this.defaultGridSize = defaultGridSize;
    }

    public BSTSection(String id, IModel<?> model) {
        super(id, model);
    }

    public IBSGridCol.BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }

    public BSTSection setDefaultGridSize(IBSGridCol.BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSTRow newRow() {
        return newRow(BSTRow::new);
    }

    public <R extends BSTRow> R newRow(IBiFunction<String, IBSGridCol.BSGridSize, R> factory) {
        return newComponent(id -> factory.apply(id, getDefaultGridSize()));
    }

    public BSTSection appendRow(IBSComponentFactory<BSTRow> factory) {
        newComponent(factory).setDefaultGridSize(getDefaultGridSize());
        return this;
    }

    public BSTDataCell newColInRow() {
        return newColInRow(BSTDataCell.MAX_COLS);
    }

    public BSTDataCell newColInRow(int colspan) {
        return newRow()
            .newCol(colspan);
    }

    @Override
    public BSTSection add(Behavior... behaviors) {
        return (BSTSection) super.add(behaviors);
    }
}
