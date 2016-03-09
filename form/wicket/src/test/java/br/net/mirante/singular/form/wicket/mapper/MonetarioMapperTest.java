package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.test.helpers.TestFinders.findId;
import static org.junit.Assert.*;

public class MonetarioMapperTest extends MapperBaseTest {

    @Override
    public void appendPackageFields(STypeComposite<? extends SIComposite> form) {
        form.addCampoMonetario("money");
    }

    @Override
    public void mockFormValues(SIComposite formInstance) {
        formInstance.setValor("money", "10,00");
    }

    @Test
    public void testEditRendering() {
        FormTester formTester = startPage(ViewMode.EDITION);
        Optional<String> money = findId(formTester.getForm(), "money");
        assertTrue(money.isPresent());
        assertTrue(formTester.getForm().get(money.get()) instanceof TextField);
    }

    @Test
    public void testVisualizationRendering() {
        FormTester formTester = startPage(ViewMode.VISUALIZATION);

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
    }

}