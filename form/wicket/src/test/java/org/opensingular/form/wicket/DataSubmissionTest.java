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

package org.opensingular.form.wicket;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;

public class DataSubmissionTest {

    private static STypeString data1, data2;
    private SingularFormDummyPageTester tester;

    private static void createTypeBuilder(STypeComposite typeBuilder) {
        data1 = typeBuilder.addFieldString("data1");
        data2 = typeBuilder.addFieldString("data2");
    }

    private static void createInstancePopulator(SIComposite instance) {
        instance.setValue("data1", "value1");
        instance.setValue("data2", "value2");
    }

    @Before
    public void setUp(){
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(DataSubmissionTest::createTypeBuilder);
        tester.getDummyPage().addInstancePopulator(DataSubmissionTest::createInstancePopulator);
    }

    // PresentsAndSubmitsData
    @Test
    public void testEditRenderingPresentsAndSubmitsData(){
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        tester.getAssertionsForm().getSubComponentWithId("data1").asTextField().assertValue().isEqualTo("value1");
        tester.getAssertionsForm().getSubComponentWithId("data2").asTextField().assertValue().isEqualTo("value2");
    }

    @Test
    public void testSubmissionUpdatesInstancePresentsAndSubmitsData(){
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        AssertionsWComponent data1Assert = tester.getAssertionsForm().getSubComponentWithId("data1").isNotNull();
        AssertionsWComponent data2Assert = tester.getAssertionsForm().getSubComponentWithId("data2").isNotNull();

        tester.newFormTester().submit();

        data1Assert.assertSInstance().isValueEquals("value1");
        data2Assert.assertSInstance().isValueEquals("value2");
    }

    // KeepsDisabledData
    @Test
    public void testEditRenderingKeepsDisabledData(){
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
        data2.asAtr().enabled(false);

        tester.getAssertionsForm().getSubComponentWithId("data1").asTextField().assertValue().isEqualTo("value1");
        tester.getAssertionsForm().getSubComponentWithId("data2").asTextField().assertValue().isEqualTo("value2");
    }

    @Test
    public void testSubmissionUpdatesInstanceKeepsDisabledData(){
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
        data2.asAtr().enabled(false);

        AssertionsWComponent data1Assert = tester.getAssertionsForm().getSubComponentWithId("data1").isNotNull();
        AssertionsWComponent data2Assert = tester.getAssertionsForm().getSubComponentWithId("data2").isNotNull();

        tester.newFormTester().submit();

        data1Assert.assertSInstance().isValueEquals("value1");
        data2Assert.assertSInstance().isValueEquals("value2");
    }

    // KeepsInvisibledData
    @Test
    public void testSubmissionUpdatesInstanceKeepsInvisibledData(){
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();
        data2.asAtr().visible(false);

        AssertionsWComponent data1Assert = tester.getAssertionsForm().getSubComponentWithId("data1");
        AssertionsWComponent data2Assert = tester.getAssertionsForm().getSubComponentWithId("data2");

        data1Assert.assertSInstance().isValueEquals("value1");
        data2Assert.assertSInstance().isValueEquals("value2");

        tester.newFormTester().submit();

        data1Assert.assertSInstance().isValueEquals("value1");
        data2Assert.assertSInstance().isValueEquals("value2");
    }

    // EreaseDependsOnData
    @Test
    public void testStopsDisplayingIt(){
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        data2.asAtr().dependsOn(data1);
        data2.asAtr().exists((x)->{
            SIComposite parent = (SIComposite) x.getParent();
            SInstance d1 =  parent.getField(data1.getNameSimple());
            if(d1 == null || d1.getValue() == null)
                return false;
            return !d1.getValue().equals("clear");
        });

        AssertionsWComponent data1Assert = tester.getAssertionsForm().getSubComponentWithId("data1");
        data1Assert.assertSInstance().isNotNull().isValueEquals("value1");
        tester.getAssertionsForm().getSubComponentWithId("data2").assertSInstance().isNotNull().isValueEquals("value2");

        tester.newFormTester().setValue(data1Assert.getTarget(), "clear");
        tester.executeAjaxEvent(data1Assert.getTarget(), SINGULAR_PROCESS_EVENT);

        tester.getAssertionsForm().getSubComponentWithId("data1").assertSInstance().isNotNull().isValueEquals("clear");
        tester.getAssertionsForm().getSubComponentWithId("data2").assertSInstance().isValueNull();
    }

}
