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

import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.junit.Test;
import org.opensingular.form.SIList;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.lib.wicket.util.output.BOutputPanel;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MultipleSelectMapperTest {

    protected SingularDummyFormPageTester createContext() {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(baseType -> {
            STypeString gadgets = baseType.addFieldString("gadget").selectionOf("iPod", "iPhone", "iMac").cast();
            STypeList<STypeString, SIString> gadgetsChoices = baseType.addFieldListOf("gadgets", gadgets);
            gadgetsChoices.selectionOf(String.class)
                    .selfIdAndDisplay()
                    .simpleProvider(ins -> Arrays.asList("iPod", "iPhone"));
            gadgetsChoices.withView(SMultiSelectionBySelectView::new);
        });
        ctx.getDummyPage().addInstancePopulator(instance -> {
            SIList gadgets = (SIList) instance.getField("gadgets");
            gadgets.addNew().setValue("iPod");
            gadgets.addNew().setValue("iPhone");
        });
        return ctx;
    }

    @Test
    public void withEditionViewTestEditRendering() {
        SingularDummyFormPageTester ctx = createContext();
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();
        ctx.getAssertionsForm().getSubCompomentWithId("gadgets").is(ListMultipleChoice.class);
    }

    @Test
    public void withVisualizationViewTestVisualizationRendering() {
        SingularDummyFormPageTester ctx = createContext();
        ctx.getDummyPage().setAsVisualizationView();
        ctx.startDummyPage();
        AssertionsWComponent panel = ctx.getAssertionsForm().getSubCompomentWithId("gadgets");
        panel.is(BOutputPanel.class);

        AssertionsWComponent output = panel.getSubCompomentWithId("output");

        assertEquals("iPod, iPhone", output.getTarget().getDefaultModelObject());
    }
}