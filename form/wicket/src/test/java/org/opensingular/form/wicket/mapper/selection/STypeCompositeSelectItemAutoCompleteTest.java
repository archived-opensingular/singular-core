package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class STypeCompositeSelectItemAutoCompleteTest {
    private SingularDummyFormPageTester tester;

    private static STypeComposite<SIComposite> base;
    private static STypeString                 name;

    private static void buildBaseType(STypeComposite<?> baseType) {
        base = baseType.addFieldComposite("myPlanets");

        name = base.addFieldString("name");
        STypeInteger position = base.addFieldInteger("position");
        STypeDecimal diameter = base.addFieldDecimal("diameterInKm");

        base.autocomplete()
                .id(name)
                .display("Planeta: ${name}, Posição: ${position}, Diametro(Km): ${diameterInKm}")
                .simpleProvider(builder -> {
                    builder.add().set(name, "Mercury").set(position, 1).set(diameter, 4879);
                    builder.add().set(name, "Venus").set(position, 2).set(diameter, 12104);
                    builder.add().set(name, "Earth").set(position, 3).set(diameter, 12756);
                });
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeCompositeSelectItemAutoCompleteTest::buildBaseType);
        tester.startDummyPage();
    }

    @Test
    public void renderOnlyLabels(){
        tester.assertContains("Mercury");
        tester.assertContains("Venus");
        tester.assertContains("Earth");
    }

    @Test
    public void submitsSelectedCompositeValue(){
        tester.newFormTester()
                .setValue(tester.getAssertionsForm().getSubComponents(TextField.class).get(1).getTarget(), "Venus")
                .submit();

        tester.getAssertionsForm().getSubCompomentWithType(base)
                .assertSInstance().isComposite().field(name.getNameSimple()).isValueEquals("Venus");
    }
}
