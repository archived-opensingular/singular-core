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
import org.apache.wicket.model.IModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.util.Shortcuts;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;

public class MasterDetailSortTest {

    private static STypeList<STypeTestMasterDetail, SIMasterDetailTest> mockMasterDetail;
    private static SingularFormDummyPageTester tester;

    private static void baseType(STypeComposite<?> mockType) {

        mockMasterDetail = mockType.addFieldListOf("mockList", STypeTestMasterDetail.class);

        STypeTestMasterDetail sTypeTestMasterDetail = mockMasterDetail.getElementsType();
        mockMasterDetail.withView(new SViewListByMasterDetail()
                .setSortableColumn(sTypeTestMasterDetail.inicio, false));

        mockMasterDetail.withInitListener(MasterDetailSortTest::fillWithBlankValues);
        mockMasterDetail.asAtr()
                .label("Mock Type Master Detail ");

    }

    private static void fillWithBlankValues(SIList<SIMasterDetailTest> list) {
        STypeTestMasterDetail type = mockMasterDetail.getElementsType();
        for (int i = 0; i < 5; i++) {
            SIComposite experiencia = list.addNew();
            LocalDate localDate = LocalDate.now();
            experiencia.setValue(type.inicio, Date.from(localDate.minusMonths(i).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    @Before
    public void setUp() {
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(MasterDetailSortTest::baseType);
        tester.startDummyPage();
    }


    @Test
    public void clickingTheButtonAddsNewItemsMasterDetail() {
        tester.getAssertionsForm().getSubComponentWithType(mockMasterDetail).assertSInstance().isList(5);
        clickAddMasterDetailButton();
        tester.getAssertionsForm().getSubComponentWithType(mockMasterDetail).assertSInstance().isList(6);

    }

    @Test
    public void sortDefaultItemsMasterDetail() {
        SIList<SInstance> instances = tester.getAssertionsForm().getSubComponentWithType(mockMasterDetail).assertSInstance().getTarget(SIList.class);
        Assert.assertEquals(5, instances.size());
        YearMonth yearMax = ((SIMasterDetailTest) instances.get(0)).getDataInicio();
        //Validar se há alguma data maior que a data do primeiro elemento[Top da lista] -> O correto é não existir.
        Assert.assertFalse(instances
                .stream()
                .map(s -> ((SIMasterDetailTest) s).getDataInicio())
                .anyMatch(dt -> yearMax.compareTo(dt) < 0));


        YearMonth newMaxYear = includeNewMaxYearElement(instances);


        IModel<SIList<SInstance>> model2 = Shortcuts.$m.loadable(() -> instances);

        MasterDetailDataProvider dataProvider = new MasterDetailDataProvider(model2, () -> (SViewListByMasterDetail) mockMasterDetail.getView());
        Iterator<SInstance> instanceIterable = dataProvider.iterator(0, 10, null, true);

        //Verifica se após a ordenação default da view [Odernação pela data de forma descrescente] se o primeiro elemento de fato é o maior elemento.
        Assert.assertTrue(newMaxYear.compareTo(((SIMasterDetailTest) instanceIterable.next()).getDataInicio()) == 0);


    }

    private YearMonth includeNewMaxYearElement(SIList<SInstance> instances) {
        YearMonth newMaxYear = YearMonth.now().plusYears(5);
        ((SIMasterDetailTest) instances.addNew()).setDataInicio(newMaxYear);
        Assert.assertEquals(6, instances.size());
        return newMaxYear;
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
