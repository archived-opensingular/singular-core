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