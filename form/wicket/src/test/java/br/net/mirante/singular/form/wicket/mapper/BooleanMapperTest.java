package br.net.mirante.singular.form.wicket.mapper;

import java.util.List;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.core.SIBoolean;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.junit.Ignore;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class BooleanMapperTest {

    public static class Default extends Base {
        @Test public void rendersSpecifiedLabel() {
            tester.assertContains("Aceito os termos e condições");
        }

        @Test public void rendersACheckBoxByDefault() {
            List<CheckBox> inputs = (List) findTag(form.getForm(), CheckBox.class);
            assertThat(inputs).hasSize(1);
        }

        @Test public void rendersACheckBoxByDefaultUnckecked() {
            assertThat(getCheckboxAt(0).getValue()).isEqualTo("");
        }

        @Test public void submitsFalseThroutghTheCheckbox() {
            form.submit();
            assertThat(baseField().getValue()).isFalse();
        }

        @Test public void submitsTrueThroutghTheCheckbox() {
            form.setValue(getCheckboxAt(0), "true");
            form.submit();
            assertThat(baseField().getValue()).isTrue();
        }
    }

    public static class TrueInstance extends Base {

        @Override
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            page.instanceCreator = (x) -> {
                SIComposite current = createInstance(x);
                current.getDescendant(field1).setValue(true);
                return current;
            };
        }

        @Test public void rendersACheckBoxCheckedWhenValueIsTrue() {
            assertThat(getCheckboxAt(0).getValue()).isEqualTo("true");
        }

    }

    @Ignore
    public static class WithRadioView extends Base {
        @Override
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            field1.withRadioView();
        }

        protected RadioChoice radioChoiceAt(int index) {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            return inputs.get(index);
        }
    }

    public static class WithRadioViewNoPreset extends WithRadioView {

        @Test public void rendersARadioChoiceIfAsked() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(inputs).hasSize(1);
            assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                    .containsOnly("1", "2");
            assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
                    .containsOnly("Sim", "Não");
        }

        @Test public void rendersNoChoiceIfNoneIsSelected() {
            assertThat(radioChoiceAt(0).getValue()).isNullOrEmpty();
        }

        @Test
        public void submitsTheValueThroughTheRadioYes() {
            selectOption(0);
            form.submit();
            assertThat(baseField().getValue()).isTrue();
        }

        @Test public void submitsTheValueThroughTheRadioNo() {
            RadioChoice choice = radioChoiceAt(0);
            selectOption(1);
            form.submit();
            assertThat(baseField().getValue()).isFalse();
        }

        private void selectOption(int index) {
            form.select(findId(form.getForm(), "aceitaTermos").get(), index);
        }
    }

    public static class WithRadioViewPresetTrue extends WithRadioView {

        @Override protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);

            page.instanceCreator = (x) -> {
                SIComposite current = createInstance(x);
                current.getDescendant(field1).setValue(true);
                return current;
            };
        }
        @Test public void rendersFalseChoiceIfFalseIsSelected() {
            assertThat(radioChoiceAt(0).getValue()).isEqualTo("1");
        }
    }

    public static class WithPersonalizedChoicesForRadioView extends Base {

        @Override
        protected void populateMockType(STypeComposite<?> mockType) {
            super.populateMockType(mockType);
            field1.withRadioView("For Sure", "No Way");
            page.instanceCreator = (x) -> {
                SIComposite current = createInstance(x);
                current.getDescendant(field1).setValue(true);
                return current;
            };
        }

        @Test public void rendersARadioChoiceWithPersonalizedLabel() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
                    .containsOnly("1", "2");
            assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
                    .containsOnly("For Sure", "No Way");
        }
    }

}

class Base extends AbstractSingularFormTest {

    protected SDictionary dictionary;
    protected STypeAttachment attachmentFileField;
    protected STypeBoolean field1;

    protected void populateMockType(STypeComposite<?> mockType) {
        field1 = mockType.addFieldBoolean("aceitaTermos");
        field1.asAtrBasic().label("Aceito os termos e condições");
    }

    protected CheckBox getCheckboxAt(int index) {
        return (CheckBox) ((List) findTag(form.getForm(), CheckBox.class)).get(index);
    }

    protected SIBoolean baseField() {
        return baseInstance().getDescendant(field1);
    }

}