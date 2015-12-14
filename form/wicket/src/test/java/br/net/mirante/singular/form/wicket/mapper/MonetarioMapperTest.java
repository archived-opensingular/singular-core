package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import org.apache.wicket.util.tester.FormTester;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MonetarioMapperTest extends MapperBaseTest {

    @Override
    public void appendInputs(MTipoComposto<? extends MIComposto> form) {
        form.addCampoMonetario("money");
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
        Optional<String> _outputmoney = findId(formTester.getForm(), "_outputmoney");
        assertFalse(money.isPresent());
        assertTrue(_outputmoney.isPresent());
    }
}