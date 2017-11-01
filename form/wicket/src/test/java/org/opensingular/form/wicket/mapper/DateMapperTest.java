/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
        tester.getAssertionsPage().getSubComponents(BSDatepickerInputGroup.class).hasSize(1);
        tester.getAssertionsForm().getSubComponentWithTypeNameSimple("data").assertSInstance().assertDateValue()
                .isInSameYearAs(isoDate)
                .isInSameDayAs(isoDate)
                .isInSameMonthAs(isoDate);
    }

    @Test
    public void viewModeRenderingTest() throws Exception {

        tester.getDummyPage().setAsVisualizationView();
        tester.startDummyPage();
        tester.getAssertionsForm().getSubComponentWithId("data")
                .isInstanceOf(BOutputPanel.class)
                .getSubComponentWithId("output")
                .assertDefaultModelObject()
                .isEqualTo("01/07/1991");
    }

}