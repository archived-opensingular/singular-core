package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

/**
 * Created by nuk on 03/02/16.
 */
public class MasterDetailMapperTest {
    protected SDictionary dict;
    protected PacoteBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    protected FormTester form;
    private STypeComposite<?> baseCompositeField, listElementType;
    private STypeBoolean field1;
    private STypeAnoMes date;
    private STypeDecimal number;
    private STypeLista<STypeComposite<SIComposite>, SIComposite> listBaseType;
    private STypeCPF cpf;

    protected void setup() {
        createBaseType();

        listBaseType = baseCompositeField.addCampoListaOfComposto("listOf", "compositeStuff");
        listElementType = listBaseType.getTipoElementos();
        date = listElementType.addCampo("date", STypeAnoMes.class);
        number = listElementType.addCampo("number", STypeDecimal.class);
        cpf = listElementType.addCampo("cpf", STypeCPF.class);

        listBaseType.withView(new MListMasterDetailView().col(date).col(number));

        setupPage();
    }

    private void createBaseType() {
        dict = SDictionary.create();
        localPackage = dict.criarNovoPacote("test");
        baseCompositeField = localPackage.createTipoComposto("group");
    }

    private void setupPage() {
        driver = new WicketTester(new TestApp());

        page = new TestPage();
        page.setDicionario(dict);
        page.setNewInstanceOfType(baseCompositeField.getNome());
    }

    protected void build() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    @Test public void rendersDataDisplayValuesOnTable(){
        setup();
        SList<SIComposite> list = page.getCurrentInstance().getDescendant(listBaseType);
        SIComposite e = list.addNovo();
        e.getDescendant(date).setValor(java.time.YearMonth.of(2016,01));
        e.getDescendant(number).setValor(2.5);
        e.getDescendant(cpf).setValor("000.111.222-33");
        build();

        driver.assertContains("01/2016");
    }
}
