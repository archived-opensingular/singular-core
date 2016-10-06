package org.opensingular.singular.form.type.core;

import org.opensingular.singular.form.*;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.internal.xml.MParser;
import org.opensingular.singular.form.io.MformPersistenciaXML;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeCompositeTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite> baseType, subStuff;
    private STypeString name, content;

    public STypeCompositeTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setup() {
        PackageBuilder pkt = createTestDictionary().createNewPackage("pkt");
        baseType = pkt.createCompositeType("baseType");
        name = baseType.addFieldString("name");
        subStuff = baseType.addFieldComposite("subStuff");
        content = subStuff.addFieldString("content");
    }

    @Test
    public void setCompositeValue() throws Exception {
        SIComposite original = baseType.newInstance();
        original.getDescendant(name).setValue("My first name");
        original.getDescendant(content).setValue("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(subStuff));

        assertThat(xml(original.getDescendant(subStuff)))
                .doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValue("My second name");
        original.getDescendant(content).setValue("My second content");

        Assertions.assertThat(original.getDescendant(name).getValue()).isEqualTo("My second name");
        Assertions.assertThat(original.getDescendant(content).getValue()).isEqualTo("My second content");

        original.getDescendant(subStuff)
                .setValue(MformPersistenciaXML.fromXML(subStuff, MParser.parse(backup)));

        Assertions.assertThat(original.getDescendant(name).getValue()).isEqualTo("My second name");
        Assertions.assertThat(original.getDescendant(content).getValue()).isEqualTo("My first content");

    }

    private String xml(SIComposite original) {
        return MformPersistenciaXML.toXML(original).toString();
    }

}
