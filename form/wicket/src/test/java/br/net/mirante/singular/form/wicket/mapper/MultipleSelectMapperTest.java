package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static org.junit.Assert.*;


public class MultipleSelectMapperTest extends MapperBaseTest {

    @Override
    public void appendPackageFields(MTipoComposto<? extends MIComposto> form) {
        MTipoString gadgets = form.addCampoString("gadget").withSelectionOf("iPod", "iPhone", "iMac");
        MTipoLista<MTipoString, MIString> gadgetsChoices = form.addCampoListaOf("gadgets", gadgets);
        gadgetsChoices.withView(MSelecaoMultiplaPorSelectView::new);
    }

    @Override
    public void mockFormValues(MIComposto formInstance) {
        MILista gadgets = (MILista) formInstance.getCampo("gadgets");
        gadgets.addNovo().setValor("iPod");
        gadgets.addNovo().setValor("iPhone");
    }

    @Test
    public void testEditRendering() {
        FormTester formTester = startPage(ViewMode.EDITION);
        Optional<String> gadgets = findId(formTester.getForm(), "gadgets");
        assertTrue(gadgets.isPresent());
        assertTrue(formTester.getForm().get(gadgets.get()) instanceof ListMultipleChoice);
    }

    @Test
    public void testVisualizationRendering() {
        FormTester formTester = startPage(ViewMode.VISUALIZATION);

        Optional<String> gadgets = findId(formTester.getForm(), "gadgets");
        assertTrue(gadgets.isPresent());

        Component panel = formTester.getForm().get(gadgets.get());
        assertNotNull(panel);
        assertTrue(panel instanceof BOutputPanel);

        Optional<String> outputFieldId = findId((BOutputPanel) panel, "output");
        assertTrue(outputFieldId.isPresent());

        Component output = panel.get(outputFieldId.get());
        assertNotNull(panel);

        assertEquals("iPod, iPhone", output.getDefaultModelObject());
    }

}