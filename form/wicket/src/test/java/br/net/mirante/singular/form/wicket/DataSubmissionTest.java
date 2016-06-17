package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

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
            assertThat(tags.get(0).getValue()).isEqualTo("value1");
            assertThat(tags.get(1).getValue()).isEqualTo("value2");
        }

        @Test public void submissionUpdatesInstance() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            TextField text1 = tags.get(0), text2 = tags.get(1);

            form.setValue(text1,"nvalue1");
            form.setValue(text2,"nvalue2");

            form.submit();

            assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("nvalue1");
            assertThat(page.getCurrentInstance().getValue(data2))
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
            assertThat(tags.get(0).getValue()).isEqualTo("value1");
            assertThat(tags.get(1).getValue()).isEqualTo("value2");
        }

        @Test public void submissionUpdatesInstance() {
            List<TextField> tags = (List) findTag(form.getForm(), TextField.class);
            TextField text1 = tags.get(0), text2 = tags.get(1);

            form.submit();

            assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            assertThat(page.getCurrentInstance().getValue(data2))
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

            assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("value2");

            form.submit();

            assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("value2");
        }
    }

    public static class EreaseDependsOnData extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            data2.asAtr().dependsOn(data1);
            data2.asAtr().visible((x)->{
                SIComposite parent = (SIComposite) x.getParent();
                SInstance d1 =  parent.getField(data1.getNameSimple());
                if(d1 == null || d1.getValue() == null) return false;
                return !d1.getValue().equals("clear");
            });
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            super.populateInstance(instance);


            page.setAsEditView();
        }

        @Test public void stopsDisplayingIt() {
            List<TextField> tags = (List) findTag(tester.getLastRenderedPage(), TextField.class);
            TextField text1 = tags.get(0), text2 = tags.get(1);

            assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("value1");
            assertThat(page.getCurrentInstance().getValue(data2))
                    .isEqualTo("value2");

            assertThat(tester.getTagById(text1.getMarkupId())).isNotNull();
            assertThat(tester.getTagById(text2.getMarkupId())).isNotNull();

            form.setValue(text1, "clear");
            tester.executeAjaxEvent(text1, IWicketComponentMapper.SINGULAR_PROCESS_EVENT);

            assertThat(findFirstFormComponentsByType(form.getForm(), data1).getValue()).isNotNull();
            assertThat(findFirstFormComponentsByType(form.getForm(), data2).getValue()).isEmpty();


            assertThat(page.getCurrentInstance().getValue(data1))
                    .isEqualTo("clear");
            assertThat(page.getCurrentInstance().getValue(data2))
                    .isNull();
        }
    }
}
