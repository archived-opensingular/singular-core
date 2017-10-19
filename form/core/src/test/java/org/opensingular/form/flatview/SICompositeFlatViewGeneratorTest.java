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

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.flatview.mapper.MockDocumentCanvas;
import org.opensingular.form.type.core.STypeString;


public class SICompositeFlatViewGeneratorTest {

    private STypeComposite<SIComposite> myComposite;
    private MockDocumentCanvas mockDocumentCanvas;
    private SICompositeFlatViewGenerator siCompositeFlatViewGenerator;

    @Before
    public void setUp() throws Exception {
        PackageBuilder myPackage = SDictionary.create().createNewPackage("br.com");
        myComposite = myPackage.createCompositeType("myComposite");
        mockDocumentCanvas = new MockDocumentCanvas();
        siCompositeFlatViewGenerator = new SICompositeFlatViewGenerator();
    }

    @Test
    public void shouldAddTitleWhenContainsLabel() throws Exception {
        myComposite.asAtr().label("My Composite");
        siCompositeFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(myComposite.newInstance()));
        mockDocumentCanvas.assertTitleCount(1);
        mockDocumentCanvas.assertTitle("My Composite");
    }

    @Test
    public void shouldNotCreateNewCanvasWhenHasNoLabel() throws Exception {
        STypeString myFieldOne = myComposite.addField("myFieldOne", STypeString.class);
        myFieldOne.asAtr().label("My Field 1");
        siCompositeFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(myComposite.newInstance()));
        mockDocumentCanvas.assertChildCount(0);
    }


}