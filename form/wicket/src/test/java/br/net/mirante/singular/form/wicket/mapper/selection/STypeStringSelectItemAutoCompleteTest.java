package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.options.SFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.mform.options.SOptionsCompositeProvider;
import br.net.mirante.singular.form.mform.util.transformer.SListBuilder;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

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
            return page.getCurrentInstance().getDescendant(base);
        }

        protected TextField fieldComponent() {
            return (TextField) ((List) findTag(form.getForm(), TextField.class)).get(0);
        }

        protected HiddenField valueComponent() {
            return (HiddenField) ((List) findTag(form.getForm(), HiddenField.class)).get(0);
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
            assertThat(findTag(form.getForm(), HiddenField.class)).hasSize(1);
        }

        @Test public void submitsSelected() {
            form.setValue(valueComponent(),"3");
            form.submit();
            assertThat(fieldInstance().getValue()).isEqualTo(OPTIONS[3-1]);
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

}
