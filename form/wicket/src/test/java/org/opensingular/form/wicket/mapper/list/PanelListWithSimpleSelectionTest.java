package org.opensingular.form.wicket.mapper.list;


import org.apache.wicket.markup.html.form.Button;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByForm;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class PanelListWithSimpleSelectionTest {

    private static STypeList<STypeComposite<SIComposite>, SIComposite> mockList;
    private static STypeComposite mockTypeComposite;
    private static STypeString simpleSelection;

    private SingularDummyFormPageTester tester;

    private static void buildBaseType(STypeComposite<?> mockType) {

        mockList = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockList.asAtr().label("Mock Type Composite");
        mockList.withView(SViewListByForm::new);

        mockTypeComposite = mockList.getElementsType();

        simpleSelection = mockTypeComposite.addFieldString("simpleSelecion");
        simpleSelection.selectionOf("a", "b", "c");
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(PanelListWithSimpleSelectionTest::buildBaseType);
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
    }

    @Test
    public void testAddItem() {
        final Button addButton = getAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(1);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(2);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(3);
    }

    @Test
    public void testRemoveItem() {
        final Button addButton = getAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(1);

        final Button removeButton = (Button) tester.getAssertionsForm()
                .findSubComponent(b -> b.getClass().getName().contains("RemoverButton")).getTarget();

        tester.executeAjaxEvent(removeButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);
    }

    @Test
    public void testAddItemAndFillOptions() {
        final Button addButton = getAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(1);

        tester.getAssertionsForm()
                .getSubCompomentWithType(mockList)
                .getSubCompomentWithType(mockTypeComposite)
                .getSubCompomentWithType(simpleSelection).assertSInstance().getTarget().setValue("a");

        tester.newFormTester().submit();

        tester.getAssertionsForm()
                .getSubCompomentWithType(mockList)
                .getSubCompomentWithType(mockTypeComposite)
                .getSubCompomentWithType(simpleSelection).assertSInstance().isNotNull().isValueEquals("a");
    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {
        final Button addButton = getAddButton();

        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(1);

        tester.getAssertionsForm()
                .getSubCompomentWithType(mockList)
                .getSubCompomentWithType(mockTypeComposite)
                .getSubCompomentWithType(simpleSelection).assertSInstance().getTarget().setValue("a");

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubCompomentWithType(mockList).assertSInstance().isList(2);

        tester.getAssertionsForm()
                .getSubCompomentWithType(mockList)
                .getSubCompomentWithType(mockTypeComposite)
                .getSubCompomentWithType(simpleSelection).assertSInstance().isValueEquals("a");
    }

    private Button getAddButton() {
        return (Button) tester.getAssertionsForm()
                .findSubComponent(b -> b.getClass().getName().contains("AddButton")).getTarget();
    }
}
