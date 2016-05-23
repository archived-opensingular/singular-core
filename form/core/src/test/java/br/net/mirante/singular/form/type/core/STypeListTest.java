package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.internal.xml.MParser;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Supplier;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeListTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite> baseType;
    private STypeString                           name, content;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listType;

    public STypeListTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setup() {
        PackageBuilder pkt  = createTestDictionary().createNewPackage("pkt");
        baseType = pkt.createCompositeType("baseType");
        name = baseType.addFieldString("name");
        listType = baseType.addFieldListOfComposite("listField", "subStuff");
        content = listType.getElementsType().addFieldString("content");
    }

    @Test
    public void setCompositeValue() throws Exception{
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
}
