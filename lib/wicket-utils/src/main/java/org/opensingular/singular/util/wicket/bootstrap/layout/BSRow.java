/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import org.opensingular.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;

public class BSRow extends BSContainer<BSRow> {

    private BSGridSize defaultGridSize;

    public BSRow(String id, BSGridSize defaultGridSize) {
        super(id);
        setDefaultGridSize(defaultGridSize);
        setCssClass("row");
    }

    public BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }
    public BSRow setDefaultGridSize(BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSRow appendColTag(int colspan, String tag, Component component) {
        return appendColTag(colspan, tag, "", component);
    }

    public BSRow appendColTag(int colspan, String tag, String attrs, Component component) {
        newCol(colspan).appendTag(tag, true, attrs, component);
        return this;
    }

    public BSRow appendCol(int colspan, IBSComponentFactory<BSCol> factory) {
        newCol(colspan);
        return this;
    }

    public BSCol newCol() {
        return newCol(BSCol.MAX_COLS);
    }

    public BSCol newCol(int colspan) {
        return newCol(colspan, BSCol::new);
    }

    public <BSC extends BSCol> BSC newCol(int colspan, IBSComponentFactory<BSC> factory) {
        BSC col = super.newComponent(factory);
        getDefaultGridSize().col(col, colspan);
        return col;
    }

    protected BSCol newCol(String id) {
        return new BSCol(id);
    }

    public BSControls newFormGroup(int colspan) {
        return newCol(colspan).newFormGroup();
    }

    @Override
    public BSRow add(Behavior... behaviors) {
        return (BSRow) super.add(behaviors);
    }
}
