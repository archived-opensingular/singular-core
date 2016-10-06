package org.opensingular.singular.form.wicket.mapper.selection;

import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opensingular.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringKeyValueRadioTest {

    private static class Base extends SingularFormBaseTest {
        protected STypeComposite<? extends SIComposite> baseCompositeField;
        protected STypeString                           tipoDeMedia;

        @Override
        protected void buildBaseType(STypeComposite<?> baseCompositeField) {
            page.enableAnnotation();

            this.baseCompositeField = baseCompositeField;
            tipoDeMedia = baseCompositeField.addFieldString("tipoDeMedia");
            tipoDeMedia.selectionOf(String.class, new SViewSelectionByRadio())
                    .selfId()
                    .display(val -> {
                        Map<String, String> displayMap = new HashMap<>();
                        displayMap.put("IMG", "Imagem");
                        displayMap.put("TXT", "Texto");
                        displayMap.put("BIN", "Binário");
                        return displayMap.get(val);
                    })
                    .simpleConverter()
                    .simpleProviderOf("IMG", "TXT", "BIN");
            tipoDeMedia.withRadioView();
            tipoDeMedia.asAtr().label("Tipo do Arquivo");
        }
    }

    public static class Default extends Base {
        @Test
        public void rendersARadioChoiceWithInformedLabels() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(inputs).hasSize(1);
            final RadioChoice radioChoice = inputs.get(0);
            assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(0), 0)).isEqualTo("IMG");
            assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(0))).isEqualTo("Imagem");
            assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(1), 1)).isEqualTo("TXT");
            assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(1))).isEqualTo("Texto");
            assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(2), 2)).isEqualTo("BIN");
            assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(2))).isEqualTo("Binário");
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
            final RadioChoice radioChoice = inputs.get(0);
            assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(0), 0)).isEqualTo("IMG");
            assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(0))).isEqualTo("Imagem");
            assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(1), 1)).isEqualTo("TXT");
            assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(1))).isEqualTo("Texto");
            assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(2), 2)).isEqualTo("BIN");
            assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(2))).isEqualTo("Binário");
        }
    }

}

