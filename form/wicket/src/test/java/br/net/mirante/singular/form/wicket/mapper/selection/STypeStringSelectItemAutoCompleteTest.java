package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findFirstComponentWithId;
import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringSelectItemAutoCompleteTest {

    //TODO: Testar modo read only

    private static class Base extends SingularFormBaseTest {

        final String[] OPTIONS = {"Bruce Wayne", "Clark Kent", "Wally West", "Oliver Queen"};
        protected STypeString base;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            base = baseType.addFieldString("myHero");
            base.withSelectionOf(OPTIONS);
            base.withView(SViewAutoComplete::new);
        }

        protected SIString fieldInstance() {
            return baseInstance(page.getCurrentInstance());
        }

        protected SIString baseInstance(SIComposite instance) {
            return instance.getDescendant(base);
        }

        protected TextField fieldComponent() {
            return (TextField) ((List) findTag(form.getForm(), TextField.class)).get(0);
        }

        protected TextField valueComponent() {
            return (TextField) findFirstFormComponentsByType(page.getForm(), base);
        }

        protected Component readOnlyComponent() {
            return findFirstComponentWithId(form.getForm(), "output");
        }

    }

    public static class Default extends Base {

        @Test public void renderOptions(){
            for(String o : OPTIONS){
                tester.assertContains(o);
            }
        }

        @Test public void renderField(){
            assertThat(findTag(form.getForm(), TextField.class)).hasSize(2);
        }

        @Test public void submitsSelected() {
            form.setValue(valueComponent(),"3");
            form.submit();
            assertThat(fieldInstance().getValue()).isEqualTo(OPTIONS[3-1]);
        }

    }

    public static class ReadOnly extends Base {
        @Override
        protected void populateInstance(SIComposite instance) {
            baseInstance(instance).setValue("Tony Stark");
            page.setAsVisualizationView();
        }

        @Test public void renderValue(){
            assertThat(readOnlyComponent().getDefaultModelObjectAsString())
                    .isEqualTo("Tony Stark");
        }
    }

    public static class KeyValueSelection extends Base {

        final String[] KEYS = {"Batman", "Superman", "Flash", "Green Arrow"};

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);

            SFixedOptionsSimpleProvider provider = base.withSelection();
            for(int i = 0 ; i < OPTIONS.length && i < KEYS.length; i++){
                provider.add(KEYS[i],OPTIONS[i]);
            }
        }

        @Test public void renderLabelsNotKeys(){
            for(String o : OPTIONS){
                tester.assertContains(o);
            }
        }

        @Test public void submitsSelectedKeyInstead() {
            form.setValue(valueComponent(),"2");
            form.submit();
            assertThat(fieldInstance().getValue()).isEqualTo(KEYS[2-1]);
        }

        @Test public void justIgnoresIfTheSelectedLabelHasNoMatch() {
            form.setValue(fieldComponent(),"Tony Stark");
            form.submit();
            assertThat(fieldInstance().getValue()).isNullOrEmpty();
        }
    }

    public static class ReadOnlyKeyValue extends Base {
        final String[] KEYS = {"Batman", "Superman", "Flash", "Green Arrow"};

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);

            SFixedOptionsSimpleProvider provider = base.withSelection();
            for(int i = 0 ; i < OPTIONS.length && i < KEYS.length; i++){
                provider.add(KEYS[i],OPTIONS[i]);
            }
            base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            baseInstance(instance).setValue("Superman");
            page.setAsVisualizationView();
        }

        @Test public void renderValue(){
            assertThat(readOnlyComponent().getDefaultModelObjectAsString())
                    .isEqualTo("Clark Kent");
        }

    }

}
