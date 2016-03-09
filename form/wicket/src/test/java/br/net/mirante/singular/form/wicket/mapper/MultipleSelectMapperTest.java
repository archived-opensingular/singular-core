package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.test.helpers.TestFinders.findId;
import static org.junit.Assert.*;


public class MultipleSelectMapperTest extends MapperBaseTest {

    @Override
    public void appendPackageFields(STypeComposite<? extends SIComposite> form) {
        STypeString gadgets = form.addCampoString("gadget").withSelectionOf("iPod", "iPhone", "iMac");
        STypeLista<STypeString, SIString> gadgetsChoices = form.addCampoListaOf("gadgets", gadgets);
        gadgetsChoices.withView(MSelecaoMultiplaPorSelectView::new);
    }

    @Override
    public void mockFormValues(SIComposite formInstance) {
        SList gadgets = (SList) formInstance.getCampo("gadgets");
        gadgets.addNovo().setValue("iPod");
        gadgets.addNovo().setValue("iPhone");
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