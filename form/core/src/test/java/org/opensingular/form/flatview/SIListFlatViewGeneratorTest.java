package org.opensingular.form.flatview;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.*;
import org.opensingular.form.flatview.mapper.MockDocumentCanvas;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

import java.util.Arrays;

public class SIListFlatViewGeneratorTest {

    private STypeComposite<SIComposite> myComposite;
    private MockDocumentCanvas mockDocumentCanvas;
    private SIListFlatViewGenerator listFlatViewGenerator;

    @Before
    public void setUp() throws Exception {
        PackageBuilder myPackage = SDictionary.create().createNewPackage("br.com");
        myComposite = myPackage.createCompositeType("myComposite");
        mockDocumentCanvas = new MockDocumentCanvas();
        listFlatViewGenerator = new SIListFlatViewGenerator();
    }

    @Test
    public void shouldPrintListLabel() throws Exception {
        STypeList<STypeString, SIString> myList = myComposite.addFieldListOf("myList", STypeString.class);
        myList.asAtr().label("My List");
        listFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(myList.newInstance()));
        mockDocumentCanvas.assertTitle("My List");
    }

    @Test
    public void shouldPrintBulletsWhenSimpleChilds() throws Exception {
        STypeList<STypeString, SIString> myList = myComposite.addFieldListOf("myList", STypeString.class);
        myList.asAtr().label("My List");
        SIList<SIString> iMyList = myList.newInstance();
        iMyList.addNew(i -> i.setValue("A"));
        iMyList.addNew(i -> i.setValue("B"));
        iMyList.addNew(i -> i.setValue("C"));
        listFlatViewGenerator.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(iMyList));
        mockDocumentCanvas.assertTitle("My List");
        mockDocumentCanvas.assertList(Arrays.asList("A", "B", "C"));
    }

    @Test
    public void shouldPrintListCountUsingListLabelWhenChildHasLabel() throws Exception {
        STypeList<STypeComposite<SIComposite>, SIComposite> myList = myComposite.addFieldListOfComposite("myList", "myCompositeElement");
        myList.asAtr().label("My List");
        myList.getElementsType().asAtr().label("My Composite Element");
        myList.getElementsType().addField("compositeElementFieldOne", STypeString.class);
        SIList<SIComposite> iMyList = myList.newInstance();
        iMyList.addNew(i -> i.getField("compositeElementFieldOne").setValue("A"));
        iMyList.addNew(i -> i.getField("compositeElementFieldOne").setValue("B"));
        iMyList.addNew(i -> i.getField("compositeElementFieldOne").setValue("C"));
        SIListFlatViewGenerator spiedListFlatViewGen = Mockito.spy(listFlatViewGenerator);
        spiedListFlatViewGen.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(iMyList));
        mockDocumentCanvas.assertTitle("My Composite Element (1 de 3)");
        mockDocumentCanvas.assertTitle("My Composite Element (2 de 3)");
        mockDocumentCanvas.assertTitle("My Composite Element (3 de 3)");
    }

    @Test
    public void shouldPrintListCountUsingListLabelWhenChildHasNoLabel() throws Exception {
        STypeList<STypeComposite<SIComposite>, SIComposite> myList = myComposite.addFieldListOfComposite("myList", "myCompositeElement");
        myList.asAtr().label("My List");
        myList.getElementsType().addField("compositeElementFieldOne", STypeString.class);
        SIList<SIComposite> iMyList = myList.newInstance();
        iMyList.addNew(i -> i.getField("compositeElementFieldOne").setValue("A"));
        iMyList.addNew(i -> i.getField("compositeElementFieldOne").setValue("B"));
        iMyList.addNew(i -> i.getField("compositeElementFieldOne").setValue("C"));
        SIListFlatViewGenerator spiedListFlatViewGen = Mockito.spy(listFlatViewGenerator);
        spiedListFlatViewGen.doWriteOnCanvas(mockDocumentCanvas, new FlatViewContext(iMyList));
        mockDocumentCanvas.assertTitle("My List (1 de 3)");
        mockDocumentCanvas.assertTitle("My List (2 de 3)");
        mockDocumentCanvas.assertTitle("My List (3 de 3)");
    }
}