package br.net.mirante.singular.form.wicket.mapper.masterdetail;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;

public class MasterDetailWithTableListWithStringTest extends AbstractSingularFormTest {

    STypeString simpleString;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockMasterDetail
                = mockType.addFieldListOfComposite("mockList", "mockTypeMasterDetailComposite");

        final STypeComposite<SIComposite> mockTypeMasterDetailComposite = mockMasterDetail.getElementsType();

        mockMasterDetail.withView(SViewListByMasterDetail::new);
        mockMasterDetail.as(AtrBasic::new)
                .label("Mock Type Master Detail ");

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockList
                = mockTypeMasterDetailComposite.addFieldListOfComposite("mockList", "mockTypeComposite");

        final STypeComposite<?> mockTypeComposite = mockList.getElementsType();

        mockList.withView(SViewListByTable::new);
        mockList.as(AtrBasic::new)
                .label("Mock Type Composite");

        simpleString = mockTypeComposite.addFieldString("mockTypeComposite", true);

    }

    @Test
    public void testAddItem() {

        final AbstractLink masterDetailaddButton = findMasterDetailLink();

        tester.executeAjaxEvent(masterDetailaddButton, "click");

        final Button tableAddButton = findTableAddButton();

        Assert.assertNotEquals(masterDetailaddButton, tableAddButton);

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(tableAddButton, "click");

        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        tester.executeAjaxEvent(tableAddButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        tester.executeAjaxEvent(tableAddButton, "click");
        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testAddItemFillValueAndThenAddOtherItem() {

        final AbstractLink masterDetailaddButton = findMasterDetailLink();

        tester.executeAjaxEvent(masterDetailaddButton, "click");

        final Button tableAddButton = findTableAddButton();

        Assert.assertNotEquals(masterDetailaddButton, tableAddButton);

        Stream<FormComponent> stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        tester.executeAjaxEvent(tableAddButton, "click");

        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final String value = "123456";

        form.setValue(getSimpleStringField(), value);

        tester.executeAjaxEvent(tableAddButton, "click");

        stream = findFormComponentsByType(form.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, getSimpleStringField().getValue());

    }

    private TextField getSimpleStringField() {
        return (TextField) findFormComponentsByType(form.getForm(), simpleString)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select simples"));
    }

    private AbstractLink findMasterDetailLink() {
        return findOnForm(AbstractLink.class, form.getForm(), (b) -> true)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar do mestre detalhe"));
    }

    private Button findTableAddButton() {
        return findOnForm(Button.class, form.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar do table list"));
    }
}
