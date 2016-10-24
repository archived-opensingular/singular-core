package org.opensingular.form.wicket.mapper;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.api.Assertions;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerInputGroup;
import org.opensingular.lib.wicket.util.output.BOutputPanel;
import org.opensingular.lib.wicket.util.util.WicketUtils;
import static org.opensingular.form.wicket.helpers.TestFinders.*;
@RunWith(Enclosed.class)
public class DateMapperTest {

    private static class Base extends SingularFormBaseTest {

        protected STypeDate dateType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            dateType = baseType.addFieldDate("data");
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

        @Test
        @SuppressWarnings("rawtypes")
        public void testEditRendering() {
            Supplier<Stream<BSDatepickerInputGroup>> datepickers = () -> findOnForm(BSDatepickerInputGroup.class, form.getForm(), IPredicate.all());
            assertTrue(datepickers.get().findAny().isPresent());
            assertTrue(datepickers.get().count() == 1);
            BSDatepickerInputGroup datepicker = datepickers.get().findFirst().get();

            Optional<TextField> textfield = WicketUtils.findFirstChild(datepicker, TextField.class);
            assertTrue(textfield.isPresent());

            Assertions.assertThat(textfield.get().getValue()).isEqualTo("01/07/1991");
        }
    }

    public static class WithVisualizationMode extends Base {

        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsVisualizationView();
        }

        @Test
        public void testVisualizationRendering() {
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