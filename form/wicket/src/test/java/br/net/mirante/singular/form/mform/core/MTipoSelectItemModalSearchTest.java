package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findTag;
import static org.fest.assertions.api.Assertions.assertThat;

public class MTipoSelectItemModalSearchTest extends SelectionFieldBaseTest {

    //    MTipoSelectItem selectType;
    protected MTipoComposto selectType;
    protected MSelecaoPorModalBuscaView view;

    @Override @SuppressWarnings({ "unchecked", "rawtypes" })
    MTipo createSelectionType(MTipoComposto group) {
        selectType = group.addCampoComposto("originUF");
//        selectType.configureKeyValueFields(); TODO: Fabs
        selectType.addCampoInteger("population").as(AtrBasic::new).label("População");;;
        selectType.addCampoInteger("areasqrkm").as(AtrBasic::new).label("Área");;;
        selectType.addCampoInteger("phonecode").as(AtrBasic::new).label("DDD");;;
        selectType.addCampoDecimal("gdp").as(AtrBasic::new).label("PIB");;;
        selectType.addCampoDecimal("hdi").as(AtrBasic::new).label("IDH");;;
        view = (MSelecaoPorModalBuscaView) selectType.setView(MSelecaoPorModalBuscaView::new);
        return selectType;
    }

    @Test public void showModalWhenClicked() {
        setupPage();
        selectType.withSelectionOf(federaldistrict(), goias());
        buildPage();

        driver.assertContainsNot("Buscar");

        clickOpenLink();

        driver.assertContains("Buscar");

        driver.assertContains("Distrito Federal");
        driver.assertContains("Goiás");
    }

    @Test public void showModalWithExtrafields(){
        setupPage();
        selectType.withSelectionOf(federaldistrict(),goias());
        view.setAdditionalFields("population","phonecode");
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

    private MSelectionableInstance federaldistrict() {
        MIComposto df = selectType.create("DF", "Distrito Federal");
        df.setValor("population",2852372);
        df.setValor("areasqrkm",5802);
        df.setValor("phonecode",61);
        df.setValor("gdp",189800000000l);
        df.setValor("hdi",0.824);
        return df;
    }

    private MSelectionableInstance goias() {
        MIComposto go = selectType.create("Go", "Goiás");
        go.setValor("population",6155998);
        go.setValor("areasqrkm", 340086);
        go.setValor("phonecode",62);
        go.setValor("gdp",57091000000l);
        go.setValor("hdi",0.735 );
        return go;
    }

    private void clickOpenLink() {
        assertThat(findTag(form.getForm(), BSDataTable.class)).isEmpty();
        List<AjaxLink> links = (List)findTag(form.getForm(), AjaxLink.class);
        assertThat(links).hasSize(1);

        driver.executeAjaxEvent(formField(form, links.get(0).getId()), "onclick");
    }
}
