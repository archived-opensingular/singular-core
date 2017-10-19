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

package org.opensingular.form.flatview.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.view.SViewTab;

import javax.annotation.Nonnull;

public class TabFlatViewGeneratorTest {

    private MockDocumentCanvas mockDocumentCanvas;
    private TabFlatViewGenerator tabFlatViewGenerator;
    private SIComposite root;

    @Before
    public void setUp() throws Exception {
        mockDocumentCanvas = Mockito.spy(new MockDocumentCanvas());
        tabFlatViewGenerator = Mockito.spy(new TabFlatViewGenerator());
    }

    @Test
    public void checkIfCreateSuperTitle() throws Exception {
        writeTabs("Seção", 0).assertTitleCount(1).assertTitle("Root");
    }

    @Test
    public void checkIfCreateTabsTitles() throws Exception {
        writeTabs("Seção", 3).assertTitleCount(4);
    }

    @Test
    public void checkIfCreateTabsWithCorrectNames() throws Exception {
        writeTabs("Prefixo", 2).assertTitle("Prefixo 1").assertTitle("Prefixo 2");
    }

    @Test
    public void checkIfCreatesNewChildForEveryTitle() throws Exception {
        writeTabs("S", 3).assertChildCount(3);
    }

    @Test
    public void checkIfCallsChildDoWrite() throws Exception {
        Mockito.doNothing().when(tabFlatViewGenerator).callChildWrite(Mockito.any(), Mockito.any(), Mockito.any());
        writeTabs("Seção", 3).assertChildCount(3);
        Mockito.verify(tabFlatViewGenerator, Mockito.times(3)).callChildWrite(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void checkIfCallsOnlyChildrenAspect() throws Exception {
        writeTabs("Seção", 3).assertChildCount(3);
        Mockito.verify(root, Mockito.times(0)).getAspect(Mockito.eq(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR));
    }

    private MockDocumentCanvas writeTabs(String prefix, int tabCount) {
        root = Mockito.spy(createRootWithTabs(prefix, tabCount).newInstance());
        tabFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(root));
        return mockDocumentCanvas;
    }


    @Nonnull
    private STypeComposite<SIComposite> createRootWithTabs(String prefix, int numberOfTabs) {
        STypeComposite<SIComposite> root = newComposite();
        SViewTab viewTab = new SViewTab();
        for (int i = 1; i <= numberOfTabs; i++) {
            viewTab.addTab(root.addFieldComposite("f" + i), prefix + " " + i);
        }
        root.withView(viewTab);
        root.asAtr().label("Root");
        return root;
    }

    private STypeComposite<SIComposite> newComposite() {
        return SDictionary.create()
                .createNewPackage("br.test")
                .createCompositeType("myComposite");
    }
}