package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

public class STypeSelectItemModalSearchTest extends SelectionFieldBaseTest {

    //    MTipoSelectItem selectType;
    protected STypeComposto selectType;
    protected MSelecaoPorModalBuscaView view;
    private STypeSimples nomeUF;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    SType createSelectionType(STypeComposto group) {
        selectType = group.addCampoComposto("originUF");
        selectType.addCampoString("id");
        nomeUF = selectType.addCampoString("nome");
        selectType.addCampoInteger("population").as(AtrBasic::new).label("População");
        selectType.addCampoInteger("areasqrkm").as(AtrBasic::new).label("Área");
        selectType.addCampoInteger("phonecode").as(AtrBasic::new).label("DDD");
        selectType.addCampoDecimal("gdp").as(AtrBasic::new).label("PIB");
        selectType.addCampoDecimal("hdi").as(AtrBasic::new).label("IDH");
        view = (MSelecaoPorModalBuscaView) selectType.setView(MSelecaoPorModalBuscaView::new);
        return selectType;
    }

    @Test
    public void showModalWhenClicked() {
        setupPage();
        selectType.withSelectionFromProvider(nomeUF, (MOptionsProvider) inst -> novoProvider(federaldistrict(), goias()));
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
        selectType.withSelectionFromProvider(nomeUF, (MOptionsProvider) inst -> novoProvider(federaldistrict(), goias()));
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

    private SList<?> novoProvider(MSelectionableInstance... selects) {
        SList lista = selectType.novaLista();
        for (MSelectionableInstance s : selects) {
            lista.addElement(s);
        }
        return lista;
    }

    private MSelectionableInstance federaldistrict() {
        SIComposite df = (SIComposite) selectType.novaInstancia();
        df.setValor("id", "DF");
        df.setValor("nome", "Distrito Federal");
        df.setValor("population", 2852372);
        df.setValor("areasqrkm", 5802);
        df.setValor("phonecode", 61);
        df.setValor("gdp", 189800000000l);
        df.setValor("hdi", 0.824);
        return df;
    }

    private MSelectionableInstance goias() {
        SIComposite go = (SIComposite) selectType.novaInstancia();
        go.setValor("id", "GO");
        go.setValor("nome", "Goiás");
        go.setValor("population", 6155998);
        go.setValor("areasqrkm", 340086);
        go.setValor("phonecode", 62);
        go.setValor("gdp", 57091000000l);
        go.setValor("hdi", 0.735);
        return go;
    }

    private void clickOpenLink() {
        assertThat(findTag(form.getForm(), BSDataTable.class)).isEmpty();
        List<AjaxLink> links = (List) findTag(form.getForm(), AjaxLink.class);
        assertThat(links).hasSize(1);

        driver.executeAjaxEvent(formField(form, links.get(0).getId()), "onclick");
    }
}
