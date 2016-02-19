package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.extractProperty;


public class STypeStringModalSearchTest extends SelectionFieldBaseTest {

    protected STypeString selectType;

    @Override @SuppressWarnings({ "unchecked", "rawtypes" })
    SType createSelectionType(STypeComposite group) {
        selectType = group.addCampoString("favoriteFruit");
        selectType.withView(MSelecaoPorModalBuscaView::new);
        return selectType;
    }

    @Test public void showModalWhenClicked(){
        setupPage();
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();

        driver.assertContainsNot("Buscar");

        clickOpenLink();

        driver.assertContains("Buscar");

        assertHasATable();

        driver.assertContains("strawberry");
        driver.assertContains("apple");
        driver.assertContains("orange");
        driver.assertContains("banana");
    }

    private void assertHasATable() {
        String responseTxt = driver.getLastResponse().getDocument();
        TagTester table = TagTester.createTagByAttribute(responseTxt,"table");

        assertThat(table).isNotNull();
    }

    @Test public void showPreviousValueWhenRendering(){
        setupPage();
        page.getCurrentInstance().setValor(selectType.getSimpleName(),"apple");
        selectType.withSelectionOf("strawberry","apple","orange","banana");
        buildPage();

        driver.assertContains("apple");
        driver.assertContainsNot("strawberry");

    }

    @Test public void showDanglingValueOnOptions(){
        setupPage();
        page.getCurrentInstance().setValor(selectType.getSimpleName(),"avocado");
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
        page.getCurrentInstance().setValor(selectType.getSimpleName(),"orange");
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

    private void clickOpenLink() {
        assertThat(findTag(form.getForm(), BSDataTable.class)).isEmpty();
        List<AjaxLink> links = (List)findTag(form.getForm(), AjaxLink.class);
        assertThat(links).hasSize(1);

        driver.executeAjaxEvent(formField(form, links.get(0).getId()), "onclick");
    }
}
