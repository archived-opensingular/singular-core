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
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.base.AbstractSingularFormTest;

public class MasterDetailWithTableListWithStringTest extends AbstractSingularFormTest {

    STypeString simpleString;

    @Override
    protected void populateMockType(STypeComposite<?> mockType) {

        final STypeLista<STypeComposite<SIComposite>, SIComposite> mockMasterDetail
                = mockType.addCampoListaOfComposto("mockList", "mockTypeMasterDetailComposite");

        final STypeComposite<SIComposite> mockTypeMasterDetailComposite = mockMasterDetail.getTipoElementos();

        mockMasterDetail.withView(MListMasterDetailView::new);
        mockMasterDetail.as(AtrBasic::new)
                .label("Mock Type Master Detail ");

        final STypeLista<STypeComposite<SIComposite>, SIComposite> mockList
                = mockTypeMasterDetailComposite.addCampoListaOfComposto("mockList", "mockTypeComposite");

        final STypeComposite<?> mockTypeComposite = mockList.getTipoElementos();

        mockList.withView(MTableListaView::new);
        mockList.as(AtrBasic::new)
                .label("Mock Type Composite");

        simpleString = mockTypeComposite.addCampoString("mockTypeComposite", true);

    }

    @Test
    public void testAddItem() {

        final AbstractLink masterDetailaddButton = findMasterDetailLink();

        wicketTester.executeAjaxEvent(masterDetailaddButton, "click");

        final Button tableAddButton = findTableAddButton();

        Assert.assertNotEquals(masterDetailaddButton, tableAddButton);

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(tableAddButton, "click");

        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        wicketTester.executeAjaxEvent(tableAddButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        wicketTester.executeAjaxEvent(tableAddButton, "click");
        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 3);

    }

    @Test
    public void testAddItemFillValueAndThenAddOtherItem() {

        final AbstractLink masterDetailaddButton = findMasterDetailLink();

        wicketTester.executeAjaxEvent(masterDetailaddButton, "click");

        final Button tableAddButton = findTableAddButton();

        Assert.assertNotEquals(masterDetailaddButton, tableAddButton);

        Stream<FormComponent> stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).isEmpty());

        wicketTester.executeAjaxEvent(tableAddButton, "click");

        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 1);

        final String value = "123456";

        formTester.setValue(getSimpleStringField(), value);

        wicketTester.executeAjaxEvent(tableAddButton, "click");

        stream = findFormComponentsByType(formTester.getForm(), simpleString);
        Assert.assertTrue(stream.collect(Collectors.toList()).size() == 2);

        Assert.assertEquals(value, getSimpleStringField().getValue());

    }

    private TextField getSimpleStringField() {
        return (TextField) findFormComponentsByType(formTester.getForm(), simpleString)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o select simples"));
    }

    private AbstractLink findMasterDetailLink() {
        return findOnForm(AbstractLink.class, formTester.getForm(), (b) -> true)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar do mestre detalhe"));
    }

    private Button findTableAddButton() {
        return findOnForm(Button.class, formTester.getForm(), b -> b.getClass().getName().contains("AddButton"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possivel encontrar o botão de adicionar do table list"));
    }
}
