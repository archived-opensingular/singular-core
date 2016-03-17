package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.util.brasil.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;
import br.net.mirante.singular.form.wicket.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;

public class MasterDetailMapperTest extends AbstractSingularFormTest {

    private STypeComposite<?> baseCompositeField;

    protected FormTester form;
    private STypeComposite<?> listElementType;
    private STypeBoolean field1;
    private STypeYearMonth date;
    private STypeDecimal number;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listBaseType;
    private STypeCPF cpf;

    protected void buildBaseType(STypeComposite<?> baseCompositeField) {
        this.baseCompositeField = baseCompositeField;
        listBaseType = baseCompositeField.addFieldListOfComposite("listOf", "compositeStuff");
        listElementType = listBaseType.getElementsType();
        date = listElementType.addField("date", STypeYearMonth.class);
        number = listElementType.addField("number", STypeDecimal.class);
        cpf = listElementType.addField("cpf", STypeCPF.class);

        listBaseType.withView(new SViewListByMasterDetail().col(date).col(number));
    }

    @Override
    protected void populateInstance(SIComposite instance) {
        SIList<SIComposite> list = instance.getDescendant(listBaseType);
        SIComposite e = list.addNew();
        e.getDescendant(date).setValue(java.time.YearMonth.of(2016,01));
        e.getDescendant(number).setValue(2.5);
        e.getDescendant(cpf).setValue("000.111.222-33");
    }

    @Test public void rendersDataDisplayValuesOnTable(){
        tester.assertContains("01/2016");
    }
}
