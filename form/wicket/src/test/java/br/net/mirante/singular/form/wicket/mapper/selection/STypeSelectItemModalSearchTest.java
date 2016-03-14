package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.options.SSelectionableInstance;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.helpers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

public class STypeSelectItemModalSearchTest extends SelectionFieldBaseTest {

    //    MTipoSelectItem selectType;
    protected STypeComposite selectType;
    protected SViewSelectionBySearchModal view;
    private STypeSimple nomeUF;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    SType createSelectionType(STypeComposite group) {
        selectType = group.addFieldComposite("originUF");
        selectType.addFieldString("id");
        nomeUF = selectType.addFieldString("nome");
        selectType.addFieldInteger("population").as(AtrBasic::new).label("População");
        selectType.addFieldInteger("areasqrkm").as(AtrBasic::new).label("Área");
        selectType.addFieldInteger("phonecode").as(AtrBasic::new).label("DDD");
        selectType.addFieldDecimal("gdp").as(AtrBasic::new).label("PIB");
        selectType.addFieldDecimal("hdi").as(AtrBasic::new).label("IDH");
        view = (SViewSelectionBySearchModal) selectType.setView(SViewSelectionBySearchModal::new);
        return selectType;
    }

    @Test
    public void showModalWhenClicked() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, (SOptionsProvider) inst -> novoProvider(federaldistrict(), goias()));
        buildPage();

        driver.assertContainsNot("Buscar");

        clickOpenLink();

        driver.assertContains("Buscar");

        driver.assertContains("Distrito Federal");
        driver.assertContains("Goiás");
    }

    @Test
    public void showModalWithExtrafields() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, (SOptionsProvider) inst -> novoProvider(federaldistrict(), goias()));
        view.setAdditionalFields("population", "phonecode");
        buildPage();

        driver.assertContainsNot("Buscar");

        clickOpenLink();

        driver.assertContains("Buscar");

        driver.assertContains("População");
        driver.assertContains("DDD");

        driver.assertContains("Distrito Federal");
        driver.assertContains("2852372");
        driver.assertContains("61");
        driver.assertContains("Goiás");
        driver.assertContains("6155998");
        driver.assertContains("62");
    }

    private SIList<?> novoProvider(SSelectionableInstance... selects) {
        SIList lista = selectType.newList();
        for (SSelectionableInstance s : selects) {
            lista.addElement(s);
        }
        return lista;
    }

    private SSelectionableInstance federaldistrict() {
        SIComposite df = (SIComposite) selectType.newInstance();
        df.setValue("id", "DF");
        df.setValue("nome", "Distrito Federal");
        df.setValue("population", 2852372);
        df.setValue("areasqrkm", 5802);
        df.setValue("phonecode", 61);
        df.setValue("gdp", 189800000000l);
        df.setValue("hdi", 0.824);
        return df;
    }

    private SSelectionableInstance goias() {
        SIComposite go = (SIComposite) selectType.newInstance();
        go.setValue("id", "GO");
        go.setValue("nome", "Goiás");
        go.setValue("population", 6155998);
        go.setValue("areasqrkm", 340086);
        go.setValue("phonecode", 62);
        go.setValue("gdp", 57091000000l);
        go.setValue("hdi", 0.735);
        return go;
    }

    private void clickOpenLink() {
        assertThat(findTag(form.getForm(), BSDataTable.class)).isEmpty();
        List<AjaxLink> links = (List) findTag(form.getForm(), AjaxLink.class);
        assertThat(links).hasSize(1);

        driver.executeAjaxEvent(formField(form, links.get(0).getId()), "onclick");
    }
}
