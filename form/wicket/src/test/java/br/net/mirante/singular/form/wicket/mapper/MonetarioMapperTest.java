package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static org.junit.Assert.*;

public class MonetarioMapperTest extends MapperBaseTest {

    @Override
    public void appendInputs(MTipoComposto<? extends MIComposto> form) {
        form.addCampoMonetario("money");
    }

    @Override
    protected void mockFormValues(MIComposto formInstance) {
        formInstance.setValor("money", "10.00");
    }

    @Override
    public void testEditRendering() {
        FormTester formTester = startPage(ViewMode.EDITION);
        Optional<String> money = findId(formTester.getForm(), "money");
        Optional<String> _outputmoney = findId(formTester.getForm(), "_outputmoney");
        assertTrue(money.isPresent());
        assertFalse(_outputmoney.isPresent());
    }

    @Override
    public void testVisualizationRendering() {
        FormTester formTester = startPage(ViewMode.VISUALIZATION);

        Optional<String> money = findId(formTester.getForm(), "money");
        assertFalse(money.isPresent());

        Optional<String> _outputmoney = findId(formTester.getForm(), "_outputmoney");
        assertTrue(_outputmoney.isPresent());

        Component panel = formTester.getForm().get(_outputmoney.get());
        assertNotNull(panel);
        assertTrue(panel instanceof BOutputPanel);

        Optional<String> outputFieldId = findId((BOutputPanel) panel, "output");
        assertTrue(outputFieldId.isPresent());

        Component output = panel.get(outputFieldId.get());
        assertNotNull(panel);

        assertEquals("R$ 10,00", output.getDefaultModelObject());
    }

}