package br.net.mirante.singular.form.wicket.mapper.selection;

/**
 * Created by nuk on 21/03/16.
 */
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringSelectItemAutoComplete {

    private static class Base extends SingularFormBaseTest {

        final String[] OPTIONS = {"Bruce Wayne", "Clark Kent", "Wally West", "Oliver Queen"};
        protected STypeString base;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            base = baseType.addFieldString("myHero");
            base.withSelectionOf(OPTIONS);
            base.withView(SViewAutoComplete::new);
        }

    }

    public static class Default extends Base {

        @Test public void renderOptions(){
            for(String o : OPTIONS){
                tester.assertContains(o);
            }
        }

        @Test public void renderField(){
            assertThat(findTag(form.getForm(), TextField.class)).hasSize(1);
        }

        @Test public void submitsSelected() {
            form.setValue(fieldComponent(),OPTIONS[1]);
            form.submit();
            assertThat(fieldInstance().getValue()).isEqualTo(OPTIONS[1]);
        }

        protected SIString fieldInstance() {
            return page.getCurrentInstance().getDescendant(base);
        }

        protected TextField fieldComponent() {
            return (TextField) ((List) findTag(form.getForm(), TextField.class)).get(0);
        }
    }

    /*public static class KeyValueSelection extends Base {

    } */

}
