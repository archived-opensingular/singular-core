package org.opensingular.singular.form.wicket.mapper.selection;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.FilteredProvider;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.singular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.singular.form.wicket.mapper.search.SearchModalPanel;
import org.opensingular.singular.util.wicket.ajax.ActionAjaxLink;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.opensingular.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringModalSearchTest {

    private static class Base extends SingularFormBaseTest {

        STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
            selectType.withView(SViewSearchModal::new);
            selectType.asAtrProvider().filteredProvider(new FilteredProvider<String>() {
                @Override
                public void configureProvider(Config cfg) {
                    cfg.getFilter().addFieldString("string");
                    cfg.result().addColumn("Fruta");
                    cfg.setCache(true);
                }

                @Override
                public List<String> load(ProviderContext<SInstance> context) {
                    return Arrays.asList("strawberry", "apple", "orange", "banana");
                }
            });
        }

        void assertHasATable() {
            String    responseTxt = tester.getLastResponse().getDocument();
            TagTester table       = TagTester.createTagByAttribute(responseTxt, "table");
            assertThat(table).isNotNull();
        }

        void clickOpenLink() {
            List<Component> search_link1 = findTag(form.getForm(), SearchModalPanel.MODAL_TRIGGER_ID, Button.class);
            ajaxClick(search_link1.get(0));
        }

    }

    public static class Default extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
        }

        @Test
        public void showModalWhenClicked() {
            tester.assertContainsNot("Filtrar");

            clickOpenLink();

            tester.assertContains("Filtrar");

            assertHasATable();

            tester.assertContains("strawberry");
            tester.assertContains("apple");
            tester.assertContains("orange");
            tester.assertContains("banana");
        }
    }

    public static class WithSelectedValue extends Base {

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue(selectType.getNameSimple(), "apple");
        }

        @Test
        public void showPreviousValueWhenRendering() {
            tester.assertContains("apple");
            tester.assertContainsNot("strawberry");
        }

        @Test
        public void changeValueWhenSelected() {
            clickOpenLink();
            List<Component> link = findTag(tester.getLastRenderedPage(), "link", ActionAjaxLink.class);
            assertThat(link).hasSize(4);

            ajaxClick(link.get(3));
            SIString selected = page.getCurrentInstance().getDescendant(selectType);
            assertThat(selected.getValue()).isEqualTo("banana");
        }
    }

    public static class WithSelecteDanglingdValue extends Base {

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue(selectType.getNameSimple(), "avocado");
        }

        @Test
        public void showDanglingValueOnOptions() {
            tester.assertContains("avocado");
        }
    }
}
