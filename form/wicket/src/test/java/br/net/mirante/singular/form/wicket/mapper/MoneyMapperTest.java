package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class MoneyMapperTest {

    private static class Base extends SingularFormBaseTest {
        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            baseType.addFieldMonetary("money");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue("money", "10,00");
        }
    }

    public static class WithEditionMode extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsEditView();
        }

        @Test
        public void testEditRendering() {
            Optional<String> money = findId(form.getForm(), "money");
            assertTrue(money.isPresent());
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            Assertions.assertThat(tags.get(0).getValue()).isEqualTo("10,00");
        }
    }



    /*@Test
    public void testVisualizationRendering() {
        FormTester formTester = startPage(ViewMode.READ_ONLY);

        Optional<String> money = findId(formTester.getForm(), "money");
        assertTrue(money.isPresent());

        Component panel = formTester.getForm().get(money.get());
        assertNotNull(panel);
        assertTrue(panel instanceof BOutputPanel);

        Optional<String> outputFieldId = findId((BOutputPanel) panel, "output");
        assertTrue(outputFieldId.isPresent());

        Component output = panel.get(outputFieldId.get());
        assertNotNull(panel);

        assertEquals("R$ 10,00", output.getDefaultModelObject());
    }*/


}