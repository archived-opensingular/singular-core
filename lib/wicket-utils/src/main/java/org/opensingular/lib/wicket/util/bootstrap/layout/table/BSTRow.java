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

package org.opensingular.lib.wicket.util.bootstrap.layout.table;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSComponentFactory;

public class BSTRow extends BSContainer<BSTRow> {

    private IBSGridCol.BSGridSize defaultGridSize;

    public BSTRow(String id, IBSGridCol.BSGridSize defaultGridSize) {
        super(id);
        setDefaultGridSize(defaultGridSize);
        setTagName("tr");
    }

    public IBSGridCol.BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }

    public BSTRow setDefaultGridSize(IBSGridCol.BSGridSize defaultGridSize) {
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
