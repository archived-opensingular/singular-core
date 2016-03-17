package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;


public class STypeStringModalSearchTest  {
    //TODO:Fabs

    private static class Base extends AbstractSingularFormTest {
        protected STypeString selectType;

        @Override
        protected void buildBaseType(STypeComposite<?> baseType) {
            selectType = baseType.addFieldString("favoriteFruit");
            selectType.withView(SViewSelectionBySearchModal::new);
        }

        protected void assertHasATable() {
            String responseTxt = tester.getLastResponse().getDocument();
            TagTester table = TagTester.createTagByAttribute(responseTxt,"table");

            assertThat(table).isNotNull();
        }

        protected void clickOpenLink() {
            assertThat(findTag(form.getForm(), BSDataTable.class)).isEmpty();
            List<AjaxLink> links = (List)findTag(form.getForm(), AjaxLink.class);
            assertThat(links).hasSize(1);

            tester.executeAjaxEvent(formField(form, links.get(0).getId()), "onclick");
        }
    }

    /*public static class Default extends Base {

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
    }*/

    /*




    @Test public void showPreviousValueWhenRendering(){
        setupPage();
        page.getCurrentInstance().setValue(selectType.getNameSimple(),"apple");
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();

        driver.assertContains("apple");
        driver.assertContainsNot("strawberry");

    }

    @Test public void showDanglingValueOnOptions(){
        setupPage();
        page.getCurrentInstance().setValue(selectType.getNameSimple(),"avocado");
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();

        clickOpenLink();

        driver.assertContains("avocado");
        driver.assertContains("strawberry");
        driver.assertContains("apple");
        driver.assertContains("orange");
        driver.assertContains("banana");

    }

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
