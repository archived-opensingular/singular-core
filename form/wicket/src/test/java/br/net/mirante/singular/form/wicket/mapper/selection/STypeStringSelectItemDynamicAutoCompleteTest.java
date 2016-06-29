package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewAutoComplete;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.core.Condition;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringSelectItemDynamicAutoCompleteTest {

    private static class Base extends SingularFormBaseTest {

        final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com"};
        protected STypeString base;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            base = baseType.addFieldString("myHero");
            base.selectionOf(DOMAINS);
            base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        }

        protected SIString fieldInstance() {
            return page.getCurrentInstance().getDescendant(base);
        }

        protected TextField fieldComponent() {
            return (TextField) ((List) findTag(form.getForm(), TextField.class)).get(0);
        }

    }

    public static class Default extends Base {

        @Test
        public void renderField() {
            assertThat(findTag(form.getForm(), TypeaheadComponent.class)).hasSize(1);
            assertThat(findTag(form.getForm(), TextField.class)).hasSize(2);
        }

        @Test
        public void haveABloodhoundBehabiour() {
            List<Component> tag = findTag(form.getForm(), TypeaheadComponent.class);
            assertThat(tag.get(0).getBehaviors()).haveAtLeast(1, new Condition<Behavior>() {
                @Override
                public boolean matches(Behavior value) {
                    return BloodhoundDataBehavior.class.isInstance(value);
                }
            });
        }

    }
}
