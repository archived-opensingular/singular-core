package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.form.wicket.mapper.search.SearchModalPanel;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class STypeStringModalSearchTest {

    private static class Base extends SingularFormBaseTest {

        STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
            selectType.withView(SViewSearchModal::new);
            selectType.asAtrProvider().filteredPagedProvider(new FilteredPagedProvider<String>() {
                @Override
                public void loadFilterDefinition(STypeComposite<?> filter) {
                    filter.addFieldString("string");
                }

                @Override
                public Long getSize(SInstance rootInstance, SInstance filter) {
                    return 4L;
                }

                @Override
                public List<String> load(SInstance rootInstance, SInstance filter, long first, long count) {
                    return Arrays.asList("strawberry", "apple", "orange", "banana");
                }

                @Override
                public List<Column> getColumns() {
                    return  Collections.singletonList(Column.of("Fruta"));
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
