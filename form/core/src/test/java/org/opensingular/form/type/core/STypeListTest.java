package org.opensingular.form.type.core;

import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.internal.xml.MParser;
import org.opensingular.form.io.MformPersistenciaXML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeListTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite> baseType;
    private STypeString name, content;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listType;

    public STypeListTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setup() {
        PackageBuilder pkt = createTestDictionary().createNewPackage("pkt");
        baseType = pkt.createCompositeType("baseType");
        name = baseType.addFieldString("name");
        listType = baseType.addFieldListOfComposite("listField", "subStuff");
        content = listType.getElementsType().addFieldString("content");
    }

    @Test
    public void setCompositeValue() throws Exception {
        SIComposite original = baseType.newInstance();
        original.getDescendant(name).setValue("My first name");
        SIComposite e1 = original.getDescendant(listType).addNew();
        e1.getDescendant(content).setValue("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(listType));

        assertThat(backup).doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValue("My second name");
        e1.getDescendant(content).setValue("My second content");

        assertThat(xml(original)).contains("My second name").contains("My second content");

        SIList<SIComposite> fromBackup = MformPersistenciaXML.fromXML(listType, MParser.parse(backup));
        original.getDescendant(listType).setValue(fromBackup);

        assertThat(xml(original)).contains("My second name").contains("My first content");

    }

    private String xml(SInstance original) {
        return MformPersistenciaXML.toXML(original).toString();
    }

    @Test public void aNewListIsEmpty() throws Exception{
        SIList<SIComposite> list = listType.newInstance();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test public void listHelpers() throws Exception{
        SIList<SIComposite> list = listType.newInstance();
        SIComposite e1 = list.addNew();
        e1.setValue(content,"abacate");
        SIComposite e2 = list.addNew();
        e2.setValue(content,"avocado");
        SIComposite e3 = list.addNew();
        e3.setValue(content,"guaca");

        assertThat(list.first()).isEqualTo(e1);
        assertThat(list.last()).isEqualTo(e3);
        list.remove(e1);
        assertThat(list.first()).isEqualTo(e2);
    }
}
