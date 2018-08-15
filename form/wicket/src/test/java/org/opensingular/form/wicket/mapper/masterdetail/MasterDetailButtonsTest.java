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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.view.list.SViewListByMasterDetail;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.resource.IconeView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MasterDetailButtonsTest {

    private static STypeList<STypeTestMasterDetail, TestSIMasterDetail> mockMasterDetailView;
    private static STypeList<STypeTestMasterDetail, TestSIMasterDetail> mockMasterDetail;
    private static SingularFormDummyPageTester tester;

    private static void baseTypeForView(STypeComposite<?> mockType) {

        mockMasterDetailView = mockType.addFieldListOf("mockMasterDetailView", STypeTestMasterDetail.class);
        mockMasterDetailView.withView(new SViewListByMasterDetail()
                .configureEditButtonPerRow(f -> false)
                .configureDeleteButtonPerRow(f -> false)
                .configureViewButtonInEditionPerRow(f -> true)
                .disableNew());

        MasterDetailButtonsTest.fillWithBlankValues(mockMasterDetailView);

    }

    private static void baseTypeDefault(STypeComposite<?> mockType) {
        mockMasterDetail = mockType.addFieldListOf("mockMasterDetail", STypeTestMasterDetail.class);
        mockMasterDetail.withView(new SViewListByMasterDetail());
    }


    private static void fillWithBlankValues(STypeList<STypeTestMasterDetail, TestSIMasterDetail> element) {
        element.withInitListener(list -> {
            STypeTestMasterDetail type = element.getElementsType();
            for (int i = 0; i < 1; i++) {
                SIComposite experiencia = list.addNew();
                LocalDate localDate = LocalDate.now();
                experiencia.setValue(type.inicio, Date.from(localDate.minusMonths(i).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        });
    }

    @Before
    public void setUp() {
        tester = new SingularFormDummyPageTester();
    }


    @Test
    public void verifyHaveJustViewAction() {
        tester.getDummyPage().setTypeBuilder(MasterDetailButtonsTest::baseTypeForView);
        tester.startDummyPage();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.EYE).isNotNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.PENCIL).isNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.REMOVE).isNull();
        AbstractLink linkAddNewElement = findMasterDetailLink();
        Assert.assertTrue(!linkAddNewElement.isVisible() || !linkAddNewElement.isVisibleInHierarchy());
    }

    @Test
    public void verifyHaveDefaultsActions() {
        //Defaults actions: Edit Remove
        tester.getDummyPage().setTypeBuilder(MasterDetailButtonsTest::baseTypeDefault);
        tester.startDummyPage();

        AbstractLink linkAddNewElement = findMasterDetailLink();
        Assert.assertTrue(linkAddNewElement.isVisible() || linkAddNewElement.isVisibleInHierarchy());
        clickAddMasterDetailButton();
        tester.getAssertionsForm().getSubComponentWithType(mockMasterDetail).assertSInstance().isList(1);

        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.EYE).isNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.PENCIL).isNotNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.REMOVE).isNotNull();
    }


    private void clickAddMasterDetailButton() {
        tester.executeAjaxEvent(findMasterDetailLink(), "click");
        tester.executeAjaxEvent(findMasterDetailModalLink(), "click");
    }

    private ActionAjaxButton findMasterDetailModalLink() {
        return tester.getAssertionsForm().findSubComponent(b -> b instanceof ActionAjaxButton).getTarget(ActionAjaxButton.class);
    }

    private AbstractLink findMasterDetailLink() {
        return tester.getAssertionsForm().getSubComponentWithId("addButton").getTarget(AbstractLink.class);
    }


}
