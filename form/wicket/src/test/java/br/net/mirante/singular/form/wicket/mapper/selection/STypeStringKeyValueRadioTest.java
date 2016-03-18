package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class STypeStringKeyValueRadioTest {

    private static class Base extends AbstractSingularFormTest {
        protected STypeComposite<? extends SIComposite> baseCompositeField;
        protected STypeString tipoDeMedia;

        @Override
        protected void buildBaseType(STypeComposite<?> baseCompositeField) {
            page.enableAnnotation();

            this.baseCompositeField = baseCompositeField;
            tipoDeMedia = baseCompositeField.addFieldString("tipoDeMedia");
            tipoDeMedia.withRadioView();
            tipoDeMedia.withSelectionFromProvider(new SOptionsProvider() {
                @Override
                public SIList<? extends SInstance> listOptions(SInstance optionsInstance) {
                    STypeString type = tipoDeMedia.getDictionary().getType(STypeString.class);
                    SIList<?> r = type.newList();
                    r.addElement(newElement(type, "IMG", "Imagem"));
                    r.addElement(newElement(type, "TXT", "Texto"));
                    r.addElement(newElement(type, "BIN", "Binário"));
                    return r;
                }

                private SIString newElement(STypeString type, String id, String label) {
                    SIString e = type.newInstance();
                    e.setValue(id);
                    e.setSelectLabel(label);
                    return e;
                }
            });
            tipoDeMedia.as(AtrBasic::new).label("Tipo do Arquivo");
        }
    }

    public static class Default extends Base {
        @Test
        public void rendersARadioChoiceWithInformedLabels() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(inputs).hasSize(1);
            assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                    .containsOnly("1", "2", "3");
            assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
                    .containsOnly("Imagem", "Texto", "Binário");
        }
    }

    public static class WithSelectedValue extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(tipoDeMedia).setValue("TXT");
        }

        @Test
        public void rendersARadioChoiceWithInformedOptionsRegardlessOfSelection() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(inputs).hasSize(1);
            assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                    .containsOnly("1", "2", "3");
            assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
                    .containsOnly("Imagem", "Texto", "Binário");
            assertThat(inputs.get(0).getValue()).isEqualTo("2");
        }
    }

}

