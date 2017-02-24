package org.opensingular.form.wicket.mapper;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerInputGroup;
import org.opensingular.lib.wicket.util.output.BOutputPanel;

public class DateMapperTest {

    private SingularDummyFormPageTester tester;

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            root.addFieldDate("data");
        });

        tester.getDummyPage().addInstancePopulator(ins -> ins.setValue("data", "01/07/1991"));
    }

    @Test
    public void editModeRenderingTest() throws Exception {

        String isoDate = "1991-07-01";

        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
        tester.getAssertionsPage().getSubComponents(BSDatepickerInputGroup.class).isSize(1);
        tester.getAssertionsForm().getSubCompomentWithTypeNameSimple("data").assertSInstance().assertDateValue()
                .isInSameYearAs(isoDate)
                .isInSameDayAs(isoDate)
                .isInSameMonthAs(isoDate);
    }

    @Test
    public void viewModeRenderingTest() throws Exception {

        tester.getDummyPage().setAsVisualizationView();
        tester.startDummyPage();
        tester.getAssertionsForm().getSubCompomentWithId("data")
                .is(BOutputPanel.class)
                .getSubCompomentWithId("output")
                .assertDefaultModelObject()
                .isEqualTo("01/07/1991");
    }

}