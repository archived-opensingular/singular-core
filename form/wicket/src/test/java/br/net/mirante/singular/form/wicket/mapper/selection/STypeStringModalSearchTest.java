package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;

@RunWith(Enclosed.class)
public class STypeStringModalSearchTest  {
    private static class Base extends SingularFormBaseTest {
        protected STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
            selectType.withView(() -> new SViewSearchModal());
        }

        protected void assertHasATable() {
            String responseTxt = tester.getLastResponse().getDocument();
            TagTester table = TagTester.createTagByAttribute(responseTxt,"table");

            assertThat(table).isNotNull();
        }

        protected void clickOpenLink() {
            List<Component> search_link1 = findTag(form.getForm(), "search_link", AjaxLink.class);
            ajaxClick(search_link1.get(0));
        }

    }

    public static class Default extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionOf("strawberry","apple","orange","banana");
        }

        @Test public void showModalWhenClicked(){
            tester.assertContainsNot("Buscar");

            clickOpenLink();

            tester.assertContains("Buscar");

            assertHasATable();

            tester.assertContains("strawberry");
            tester.assertContains("apple");
            tester.assertContains("orange");
            tester.assertContains("banana");
        }
    }

    public static class WithSelectedValue extends Base {

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionOf("strawberry", "apple", "orange", "banana");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue(selectType.getNameSimple(),"apple");
        }

        @Test public void showPreviousValueWhenRendering(){
            tester.assertContains("apple");
            tester.assertContainsNot("strawberry");
        }

        @Test public void changeValueWhenSelected(){
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
        protected void buildBaseType(STypeComposite<?> baseType) {
            super.buildBaseType(baseType);
            selectType.withSelectionOf("strawberry", "apple", "orange", "banana");
        }

        @Override
        protected void populateInstance(SIComposite instance) {
            instance.setValue(selectType.getNameSimple(),"avocado");
        }

        @Test public void showDanglingValueOnOptions(){
            clickOpenLink();

            tester.assertContains("avocado");
            tester.assertContains("strawberry");
            tester.assertContains("apple");
            tester.assertContains("orange");
            tester.assertContains("banana");
        }
    }

    /*








    @Ignore("Must understand how to handle the ajax modal and its actions")
    @Test public void changeValueWhenSelected(){
        setupPage();
        page.getCurrentInstance().setValue(selectType.getNameSimple(),"orange");
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();
        assertThat(page.size()).isEqualTo(3);

        clickOpenLink();

        final Component[] modal = new Component[]{null};
        page.visitChildren((x,y) -> {
            if(x.getId().endsWith("_modal")){
                modal[0] = x;
            }
        });

    }


*/}
