package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import br.net.mirante.singular.util.wicket.output.BOutputPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.tester.FormTester;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class DateMapperTest  {

    private static class Base extends AbstractSingularFormTest {

        protected STypeDate dateType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            dateType = baseType.addFieldData("data");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue("data", "01/07/1991");
        }
    }

    public static class WithEditMode extends Base {

        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsEditView();
        }

        @Test public void testEditRendering() {
            assertTrue(findId(form.getForm(), "data").isPresent());

            List<TextField> r = (List) findTag(form.getForm(), "data", TextField.class);
            Assertions.assertThat(r).hasSize(1);
            Assertions.assertThat(r.get(0).getValue()).isEqualTo("01/07/1991");
        }
    }

    public static class WithVisualizationMode extends Base {

        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsVisualizationView();
        }

        @Test public void testVisualizationRendering() {
            Optional<String> data = findId(form.getForm(), "data");
            assertTrue(data.isPresent());

            Component panel = form.getForm().get(data.get());
            assertNotNull(panel);
            assertTrue(panel instanceof BOutputPanel);

            Optional<String> outputFieldId = findId((BOutputPanel) panel, "output");
            assertTrue(outputFieldId.isPresent());

            Component output = panel.get(outputFieldId.get());
            assertNotNull(panel);

            assertEquals("01/07/1991", output.getDefaultModelObject());
        }
    }


}