package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.markup.html.form.RadioChoice;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.wicket.helpers.AssertionsWComponentList;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeStringKeyValueRadioTest {
    private static STypeString tipoDeMedia;

    private SingularDummyFormPageTester tester;

    private static void buildBaseType(STypeComposite<?> baseCompositeField) {
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

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeStringKeyValueRadioTest::buildBaseType);
        tester.getDummyPage().enableAnnotation();
    }

    @Test
    public void rendersARadioChoiceWithInformedLabels() {
        tester.startDummyPage();

        AssertionsWComponentList radioListAssertion = tester.getAssertionsForm().getSubComponents(RadioChoice.class);

        radioListAssertion.isSize(1);
        RadioChoice radioChoice = radioListAssertion.get(0).getTarget(RadioChoice.class);

        assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(0), 0)).isEqualTo("IMG");
        assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(0))).isEqualTo("Imagem");
        assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(1), 1)).isEqualTo("TXT");
        assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(1))).isEqualTo("Texto");
        assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(2), 2)).isEqualTo("BIN");
        assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(2))).isEqualTo("Binário");
    }

    @Test
    public void rendersARadioChoiceWithInformedOptionsRegardlessOfSelection() {
        tester.getDummyPage().addInstancePopulator(instance ->instance.getDescendant(tipoDeMedia).setValue("TXT"));
        tester.startDummyPage();

        AssertionsWComponentList radioListAssertion = tester.getAssertionsForm().getSubComponents(RadioChoice.class);

        radioListAssertion.isSize(1);
        RadioChoice radioChoice = radioListAssertion.get(0).getTarget(RadioChoice.class);
        assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(0), 0)).isEqualTo("IMG");
        assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(0))).isEqualTo("Imagem");
        assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(1), 1)).isEqualTo("TXT");
        assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(1))).isEqualTo("Texto");
        assertThat(radioChoice.getChoiceRenderer().getIdValue(radioChoice.getChoices().get(2), 2)).isEqualTo("BIN");
        assertThat(radioChoice.getChoiceRenderer().getDisplayValue(radioChoice.getChoices().get(2))).isEqualTo("Binário");
    }

}

