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

package org.opensingular.lib.wicket.util.bootstrap.layout;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol.BSGridSize;

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
