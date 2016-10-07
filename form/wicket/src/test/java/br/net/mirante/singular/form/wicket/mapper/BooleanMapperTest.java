package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.SIBoolean;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

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
        protected void buildBaseType(STypeComposite<?> mockType) {
            super.buildBaseType(mockType);
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(field1).setValue(true);
        }

        @Test public void rendersACheckBoxCheckedWhenValueIsTrue() {
            assertThat(getCheckboxAt(0).getValue()).isEqualTo("true");
        }

    }

    public static class WithRadioViewNoPreset extends WithRadioView {

        @Test public void rendersARadioChoiceIfAsked() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(inputs).hasSize(1);
            assertThat(inputs.get(0).getChoices()).containsOnly("Sim", "Não");
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

        @Override protected void buildBaseType(STypeComposite<?> mockType) {
            super.buildBaseType(mockType);

        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(field1).setValue(true);
        }

        @Test public void rendersFalseChoiceIfFalseIsSelected() {
            assertThat(radioChoiceAt(0).getValue()).isEqualTo("Sim");
        }
    }

    public static class WithPersonalizedChoicesForRadioView extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> mockType) {
            super.buildBaseType(mockType);
            field1.withRadioView("For Sure", "No Way");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.getDescendant(field1).setValue(true);
        }

        @Test public void rendersARadioChoiceWithPersonalizedLabel() {
            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
            assertThat(inputs.get(0).getChoices()).containsOnly("For Sure", "No Way");
        }
    }

}

class Base extends SingularFormBaseTest {

    protected SDictionary dictionary;
    protected STypeAttachment attachmentFileField;
    protected STypeBoolean field1;

    protected void buildBaseType(STypeComposite<?> mockType) {
        field1 = mockType.addFieldBoolean("aceitaTermos");
        field1.asAtr().label("Aceito os termos e condições");
    }

    protected CheckBox getCheckboxAt(int index) {
        return (CheckBox) ((List) findTag(form.getForm(), CheckBox.class)).get(index);
    }

    protected SIBoolean baseField() {
        return baseInstance().getDescendant(field1);
    }

}

class WithRadioView extends Base {
    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {
        super.buildBaseType(mockType);
        field1.withRadioView();
    }

    protected RadioChoice radioChoiceAt(int index) {
        List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
        return inputs.get(index);
    }
}