package org.opensingular.form.type.core;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.opensingular.form.io.SFormXMLUtil;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeCompositeTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite> baseType, subStuff;
    private STypeString name, content;

    public STypeCompositeTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        PackageBuilder pkt = createTestPackage();
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
                .setValue(SFormXMLUtil.fromXML(subStuff, MParser.parse(backup)));

        Assertions.assertThat(original.getDescendant(name).getValue()).isEqualTo("My second name");
        Assertions.assertThat(original.getDescendant(content).getValue()).isEqualTo("My first content");

    }

    private String xml(SIComposite original) {
        return SFormXMLUtil.toXML(original).get().toString();
    }

}
