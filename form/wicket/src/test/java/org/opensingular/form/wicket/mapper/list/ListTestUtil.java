/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.list;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.list.AbstractSViewListWithControls;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.form.wicket.mapper.buttons.AddButton;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;

public class ListTestUtil {

    private static STypeList<STypeString, SIString> buildTableForButons;


    public static void buildTableForButons(STypeComposite<?> mockType, ISupplier<? extends AbstractSViewListWithControls> sViewListByTable) {
        buildTableForButons = mockType.addFieldListOf("buildTableForButons", STypeString.class);
        buildTableForButons.withView(sViewListByTable);
        ListTestUtil.fillWithBlankValues(buildTableForButons);
    }


    private static void fillWithBlankValues(STypeList<STypeString, SIString> element) {
        element.withInitListener(list -> list.addNew().setValue("01"));
    }

    public static AjaxLink findAddButton(SingularFormDummyPageTester tester) {
        return tester.getAssertionsForm().findSubComponent(b -> b instanceof AddButton).getTarget(AjaxLink.class);
    }

    public static AbstractLink findMasterDetailLink(SingularFormDummyPageTester tester) {
        return tester.getAssertionsForm().getSubComponentWithId("addButton").getTarget(AbstractLink.class);
    }

    public static void clickAddMasterDetailButton(SingularFormDummyPageTester tester) {
        tester.executeAjaxEvent(findMasterDetailLink(tester), "click");
        tester.executeAjaxEvent(findMasterDetailModalLink(tester), "click");
    }

    public static ActionAjaxButton findMasterDetailModalLink(SingularFormDummyPageTester tester) {
        return tester.getAssertionsForm().findSubComponent(b -> b instanceof ActionAjaxButton).getTarget(ActionAjaxButton.class);
    }

    public static STypeList<STypeString, SIString> getBuildTableForButons() {
        return buildTableForButons;
    }
}
