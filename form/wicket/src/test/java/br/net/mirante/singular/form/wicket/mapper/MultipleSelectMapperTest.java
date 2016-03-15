package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static org.junit.Assert.*;


public class MultipleSelectMapperTest extends MapperBaseTest {

    @Override
    public void appendPackageFields(STypeComposite<? extends SIComposite> form) {
        STypeString gadgets = form.addFieldString("gadget").withSelectionOf("iPod", "iPhone", "iMac");
        STypeList<STypeString, SIString> gadgetsChoices = form.addFieldListOf("gadgets", gadgets);
        gadgetsChoices.withView(SMultiSelectionBySelectView::new);
    }

    @Override
    public void mockFormValues(SIComposite formInstance) {
        SIList gadgets = (SIList) formInstance.getField("gadgets");
        gadgets.addNew().setValue("iPod");
        gadgets.addNew().setValue("iPhone");
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