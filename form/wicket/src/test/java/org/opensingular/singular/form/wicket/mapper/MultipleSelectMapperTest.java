package org.opensingular.singular.form.wicket.mapper;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Optional;

import static org.opensingular.singular.form.wicket.helpers.TestFinders.findId;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class MultipleSelectMapperTest {
    private static class Base extends SingularFormBaseTest {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            STypeString gadgets = baseType.addFieldString("gadget").selectionOf("iPod", "iPhone", "iMac").cast();
            STypeList<STypeString, SIString> gadgetsChoices = baseType.addFieldListOf("gadgets", gadgets);
            gadgetsChoices.selectionOf(String.class)
                    .selfIdAndDisplay()
                    .simpleProvider(ins -> Arrays.asList("iPod", "iPhone"));
            gadgetsChoices.withView(SMultiSelectionBySelectView::new);
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            SIList gadgets = (SIList) instance.getField("gadgets");
            gadgets.addNew().setValue("iPod");
            gadgets.addNew().setValue("iPhone");
        }
    }

    public static class WithEditionView extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsEditView();
        }

        @Test public void testEditRendering() {
            Optional<String> gadgets = findId(form.getForm(), "gadgets");
            assertTrue(gadgets.isPresent());
            assertTrue(form.getForm().get(gadgets.get()) instanceof ListMultipleChoice);
        }
    }

    public static class WithVisualizationView extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsVisualizationView();
        }

        @Test public void testVisualizationRendering() {
            Optional<String> gadgets = findId(form.getForm(), "gadgets");
            assertTrue(gadgets.isPresent());

            Component panel = form.getForm().get(gadgets.get());
            assertNotNull(panel);
            assertTrue(panel instanceof BOutputPanel);

            Optional<String> outputFieldId = findId((BOutputPanel) panel, "output");
            assertTrue(outputFieldId.isPresent());

            Component output = panel.get(outputFieldId.get());
            assertNotNull(panel);

            assertEquals("iPod, iPhone", output.getDefaultModelObject());
        }
    }







}