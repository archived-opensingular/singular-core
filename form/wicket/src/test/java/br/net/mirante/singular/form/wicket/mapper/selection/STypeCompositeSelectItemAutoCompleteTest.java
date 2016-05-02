package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeCompositeSelectItemAutoCompleteTest {

    private static class Base extends SingularFormBaseTest {

        protected STypeComposite<SIComposite> base;
        protected STypeString                 name;
        protected STypeInteger                position;
        protected STypeDecimal                diameter;

        @Override
        public void buildBaseType(STypeComposite<?> baseType) {
            base = baseType.addFieldComposite("myPlanets");

            name = base.addFieldString("name");
            position = base.addFieldInteger("position");
            diameter = base.addFieldDecimal("diameterInKm");

            base.autocomplete()
                    .id(name)
                    .display("Planeta: ${name}, Posição: ${position}, Diametro(Km): ${diameterInKm}")
                    .provider(builder -> {
                        builder.add().set(name, "Mercury").set(position, 1).set(diameter, 4879);
                        builder.add().set(name, "Venus").set(position, 2).set(diameter, 12104);
                        builder.add().set(name, "Earth").set(position, 3).set(diameter, 12756);
                    });
        }

        protected TextField valueComponent() {
            return (TextField) findFirstFormComponentsByType(page.getForm(), base);
        }

        protected SIComposite fieldInstance() {
            return page.getCurrentInstance().getDescendant(base);
        }

    }

    public static class Default extends Base {
        @Test
        public void renderOnlyLabels() {
            tester.assertContains("Mercury");
            tester.assertContains("Venus");
            tester.assertContains("Earth");
        }

        @Test
        public void submitsSelectedCompositeValue() {
            form.setValue(valueComponent(), "Venus");
            form.submit();
            assertThat(fieldInstance().getDescendant(name).getValue()).isEqualTo("Venus");
        }
    }
}
