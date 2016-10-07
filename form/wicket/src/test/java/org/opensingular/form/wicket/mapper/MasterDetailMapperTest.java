package org.opensingular.form.wicket.mapper;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class MasterDetailMapperTest extends SingularFormBaseTest {

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
