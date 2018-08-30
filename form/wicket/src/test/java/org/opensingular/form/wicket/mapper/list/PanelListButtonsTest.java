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


import org.apache.wicket.markup.html.link.AbstractLink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.view.list.ButtonsConfigWithInsert;
import org.opensingular.form.view.list.SViewListByForm;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.form.wicket.mapper.buttons.InserirButton;
import org.opensingular.form.wicket.mapper.buttons.RemoverButton;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

public class PanelListButtonsTest {

    protected SingularFormDummyPageTester tester;

    @Before
    public void setUp() {
        tester = new SingularFormDummyPageTester();
    }

    @Test
    public void verifyDontHaveActionButton() {


        ISupplier<SViewListByForm> viewListByForm = (ISupplier<SViewListByForm>) () -> new SViewListByForm()
                .disableDelete()
                .disableAdd();

        tester.getDummyPage().setTypeBuilder(m -> ListTestUtil.buildTableForButons(m, viewListByForm));
        tester.startDummyPage();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof InserirButton).isNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof RemoverButton).isNull();
        AbstractLink linkAddNewElement = ListTestUtil.findAddButton(tester);
        Assert.assertTrue(!linkAddNewElement.isVisible() || !linkAddNewElement.isVisibleInHierarchy());
    }

    @Test
    public void verifyHaveAllActionButtons() {

        ISupplier<SViewListByForm> viewListByForm = (ISupplier<SViewListByForm>) () -> new SViewListByForm()
                .enableInsert(ButtonsConfigWithInsert.INSERT_HINT, null, DefaultIcons.PUZZLE);

        //Table List contains 3 buttons : Edit, New, Remove
        tester.getDummyPage().setTypeBuilder(m -> ListTestUtil.buildTableForButons(m, viewListByForm));
        tester.startDummyPage();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof InserirButton).isNotNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof RemoverButton).isNotNull();
        AbstractLink linkAddNewElement = ListTestUtil.findAddButton(tester);
        Assert.assertTrue(linkAddNewElement.isVisible() || linkAddNewElement.isVisibleInHierarchy());
    }

}
