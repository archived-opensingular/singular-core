package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class MasterDetailMapperTest extends AbstractWicketFormTest {

    protected PackageBuilder localPackage;
    protected WicketTester driver;
    protected TestPage page;
    private STypeComposite<?> baseCompositeField;

    protected FormTester form;
    private STypeComposite<?> listElementType;
    private STypeBoolean field1;
    private STypeAnoMes date;
    private STypeDecimal number;
    private STypeLista<STypeComposite<SIComposite>, SIComposite> listBaseType;
    private STypeCPF cpf;

    protected void setup() {
        localPackage = dicionario.createNewPackage("test");
        baseCompositeField = localPackage.createTipoComposto("group");

        loadTestType(baseCompositeField);

        setupPage();
    }

    private void setupPage() {
        driver = new WicketTester(new TestApp());

        page = new TestPage();
        page.setIntance(createIntance(() -> baseCompositeField));
    }

    protected void build() {
        page.build();
        driver.startPage(page);

        form = driver.newFormTester("test-form", false);
    }

    private void loadTestType(STypeComposite<?> baseCompositeField) {
        listBaseType = baseCompositeField.addCampoListaOfComposto("listOf", "compositeStuff");
        listElementType = listBaseType.getTipoElementos();
        date = listElementType.addCampo("date", STypeAnoMes.class);
        number = listElementType.addCampo("number", STypeDecimal.class);
        cpf = listElementType.addCampo("cpf", STypeCPF.class);

        listBaseType.withView(new MListMasterDetailView().col(date).col(number));
    }

    @Test public void rendersDataDisplayValuesOnTable(){
        setup();
        SList<SIComposite> list = page.getCurrentInstance().getDescendant(listBaseType);
        SIComposite e = list.addNovo();
        e.getDescendant(date).setValue(java.time.YearMonth.of(2016,01));
        e.getDescendant(number).setValue(2.5);
        e.getDescendant(cpf).setValue("000.111.222-33");
        build();

        driver.assertContains("01/2016");
    }
}
