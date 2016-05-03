package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.form.wicket.mapper.MoneyMapperTest;
import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.junit.Assert.assertTrue;

/**
 * Created by nuk on 02/05/16.
 */
@RunWith(Enclosed.class)
public class DataSubmissionTest {

    private static class Base extends SingularFormBaseTest {

        protected STypeString data1, data2;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            data1 = baseType.addFieldString("data1");
            data2 = baseType.addFieldString("data2");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue("data1", "value1");
            instance.setValue("data2", "value2");
        }
    }

    public static class PresentsAndSubmitsData extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            page.setAsEditView();
        }

        @Test public void testEditRendering() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            Assertions.assertThat(tags.get(0).getValue()).isEqualTo("value1");
            Assertions.assertThat(tags.get(1).getValue()).isEqualTo("value2");
        }

        @Test public void submissionUpdatesInstance() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            TextField text1 = tags.get(0), text2 = tags.get(1);

            form.setValue(text1,"nvalue1");
            form.setValue(text2,"nvalue2");

            form.submit();

            Assertions.assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("nvalue1");
            Assertions.assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("nvalue2");
        }
    }

    public static class KeepsDisabledData extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);
            data2.asAtr().enabled(false);
            page.setAsEditView();
        }

        @Test public void testEditRendering() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            Assertions.assertThat(tags.get(0).getValue()).isEqualTo("value1");
            Assertions.assertThat(tags.get(1).getValue()).isEqualTo("value2");
        }

        @Test public void submissionUpdatesInstance() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            TextField text1 = tags.get(0), text2 = tags.get(1);

            form.submit();

            Assertions.assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            Assertions.assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("value2");
        }
    }

    public static class KeepsInvisibledData extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);

            data2.asAtr().visible((x)->{
                return false;
            });
            page.setAsEditView();
        }

        @Test public void submissionUpdatesInstance() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            TextField text1 = tags.get(0);

            Assertions.assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            Assertions.assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("value2");

            form.submit();

            Assertions.assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            Assertions.assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("value2");
        }
    }
}
