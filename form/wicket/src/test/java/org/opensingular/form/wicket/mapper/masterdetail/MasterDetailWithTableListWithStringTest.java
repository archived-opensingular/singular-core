package org.opensingular.form.wicket.mapper.masterdetail;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import static org.fest.assertions.api.Assertions.assertThat;

public class MasterDetailWithTableListWithStringTest {

    private static SingularDummyFormPageTester tester;

    private static STypeList<STypeComposite<SIComposite>, SIComposite> mockList;
    private static STypeComposite<?> mockTypeComposite;
    private static STypeString simpleString;

    private static void baseType(STypeComposite<?> mockType) {

        final STypeList<STypeComposite<SIComposite>, SIComposite> mockMasterDetail
                = mockType.addFieldListOfComposite("mockList", "mockTypeMasterDetailComposite");

        final STypeComposite<SIComposite> mockTypeMasterDetailComposite = mockMasterDetail.getElementsType();

        mockMasterDetail.withView(SViewListByMasterDetail::new);
        mockMasterDetail.asAtr()
                .label("Mock Type Master Detail ");

        mockList = mockTypeMasterDetailComposite.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockTypeComposite = mockList.getElementsType();

        mockList.withView(SViewListByTable::new);
        mockList.asAtr()
                .label("Mock Type Composite");

        simpleString = mockTypeComposite.addFieldString("mockTypeComposite");
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(MasterDetailWithTableListWithStringTest::baseType);
        tester.startDummyPage();
    }

    @Test
    public void clickingTheButtonAddsNewItems() {
        clickMasterDetailLink();

        assertThat(findMasterDetailLink()).isNotEqualTo(findTableAddButton());

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);
        clickAddButton();
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(1);

        clickAddButton();
        clickAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(3);
    }

    @Test
    public void keepsFilledDataForAlreadyAddedItems() {
        clickMasterDetailLink();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);

        clickAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(1);
        tester.getAssertionsForm().getSubCompomentWithType(simpleString).assertSInstance().getTarget().setValue("123456");

        clickAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(2);
        tester.getAssertionsForm().getSubCompomentWithType(simpleString).assertSInstance().isValueEquals("123456");
    }

    private void clickMasterDetailLink() {
        tester.executeAjaxEvent(findMasterDetailLink(), "click");
    }

    private void clickAddButton() {
        tester.executeAjaxEvent(findTableAddButton(), "click");
    }

    private AbstractLink findMasterDetailLink() {
        return (AbstractLink) tester.getAssertionsForm().findSubComponent((b) -> b.getId().equals("addButton")).getTarget();
    }

    private Button findTableAddButton() {
        return (Button) tester.getAssertionsForm().findSubComponent(b -> b.getClass().getName().contains("AddButton")).getTarget();
    }
}
