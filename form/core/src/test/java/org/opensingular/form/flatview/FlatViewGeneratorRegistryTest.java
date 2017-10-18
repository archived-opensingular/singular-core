/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.flatview;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.*;
import org.opensingular.form.flatview.mapper.BlockFlatViewGenerator;
import org.opensingular.form.flatview.mapper.SelectionFlatViewGenerator;
import org.opensingular.form.flatview.mapper.TabFlatViewGenerator;
import org.opensingular.form.flatview.mapper.TableFlatViewGenerator;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.view.SViewTab;

import static org.junit.Assert.*;

public class FlatViewGeneratorRegistryTest {
    PackageBuilder fooBarpackage;

    @Before
    public void setUp() throws Exception {
        fooBarpackage = SDictionary.create().createNewPackage("foo.bar");
    }

    @Test
    public void testRetrieveByBlock() throws Exception {
        STypeComposite<SIComposite> foobar = fooBarpackage.createCompositeType("foobar");
        foobar.withView(new SViewByBlock());
        assertThat(foobar.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(BlockFlatViewGenerator.class));
    }

    @Test
    public void testRetrieveByTab() throws Exception {
        STypeComposite<SIComposite> foobar = fooBarpackage.createCompositeType("foobar");
        foobar.withView(new SViewTab());
        assertThat(foobar.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(TabFlatViewGenerator.class));
    }

    @Test
    public void testRetrieveBySelection() throws Exception {
        STypeComposite<SIComposite> foobar = fooBarpackage.createCompositeType("foobar");
        foobar.withView(new SViewSelectionBySelect());
        assertThat(foobar.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(SelectionFlatViewGenerator.class));
    }

    @Test
    public void testRetrieveByTable() throws Exception {
        STypeList<STypeComposite<SIComposite>, SIComposite> foobars = fooBarpackage.createListOfNewCompositeType("foobars", "foobar");
        foobars.withView(new SViewListByTable());
        assertThat(foobars.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null), Matchers.instanceOf(TableFlatViewGenerator.class));
    }

}
