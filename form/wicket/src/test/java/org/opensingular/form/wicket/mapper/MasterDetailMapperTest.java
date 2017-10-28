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

import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class MasterDetailMapperTest {

    private static STypeList<STypeComposite<SIComposite>, SIComposite> listBaseType;
    private static STypeComposite<?> listElementType;
    private static STypeYearMonth date;
    private static STypeDecimal number;
    private static STypeCPF cpf;

    private static void buildBaseType(STypeComposite<?> baseCompositeField) {
        listBaseType = baseCompositeField.addFieldListOfComposite("listOf", "compositeStuff");
        listElementType = listBaseType.getElementsType();
        date = listElementType.addField("date", STypeYearMonth.class);
        number = listElementType.addField("number", STypeDecimal.class);
        cpf = listElementType.addField("cpf", STypeCPF.class);

        listBaseType.withView(new SViewListByMasterDetail().col(date).col(number));
    }

    private static void populateInstance(SIComposite instance) {
        SIList<SIComposite> list = instance.getDescendant(listBaseType);
        SIComposite e = list.addNew();
        e.getDescendant(date).setValue(java.time.YearMonth.of(2016,01));
        e.getDescendant(number).setValue(2.5);
        e.getDescendant(cpf).setValue("000.111.222-33");
    }

    @Test
    public void rendersDataDisplayValuesOnTable(){
        SingularDummyFormPageTester test = new SingularDummyFormPageTester();

        test.getDummyPage().setTypeBuilder(MasterDetailMapperTest::buildBaseType);
        test.getDummyPage().addInstancePopulator(MasterDetailMapperTest::populateInstance);
        test.startDummyPage();

        AssertionsWComponent compositeAssertion = test.getAssertionsForm().getSubComponentWithType(listBaseType)
                .getSubComponentWithType(listElementType).isNotNull();

        compositeAssertion.getSubComponentWithType(date).assertSInstance().isValueEquals(
                java.time.YearMonth.of(2016, 01));
        //        compositeAssertion.getSubComponentWithType(number).assertSInstance().isValueEquals(new BigDecimal(2.5));
//        compositeAssertion.getSubComponentWithType(cpf).assertSInstance().isValueEquals("000.111.222-33");

    }
}
