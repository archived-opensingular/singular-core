package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.TextField;
import org.fest.assertions.core.Condition;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.opensingular.form.wicket.helpers.TestFinders.findTag;

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
            return findTag(form.getForm(), TextField.class).get(0);
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
            List<TypeaheadComponent> tag = findTag(form.getForm(), TypeaheadComponent.class);
            assertThat(tag.get(0).getBehaviors()).haveAtLeast(1, new Condition<Behavior>() {
                @Override
                public boolean matches(Behavior value) {
                    return BloodhoundDataBehavior.class.isInstance(value);
                }
            });
        }

    }
}
