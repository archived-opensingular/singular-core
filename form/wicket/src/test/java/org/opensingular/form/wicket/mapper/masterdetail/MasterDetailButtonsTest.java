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
import org.opensingular.form.view.list.SViewListByMasterDetail;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.form.wicket.mapper.list.ListTestUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.resource.IconeView;

public class MasterDetailButtonsTest {

    private static SingularFormDummyPageTester tester;

    @Before
    public void setUp() {
        tester = new SingularFormDummyPageTester();
    }

    @Test
    public void verifyHaveJustViewAction() {
        ISupplier<SViewListByMasterDetail> viewListByMasterDetail =  (ISupplier<SViewListByMasterDetail>) () -> new SViewListByMasterDetail()
                .disableEdit()
                .disableDelete()
                .enableView()
                .disableNew();

        tester.getDummyPage().setTypeBuilder(s -> ListTestUtil.buildTableForButons(s, viewListByMasterDetail));
        tester.startDummyPage();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.EYE).isNotNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.PENCIL).isNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.REMOVE).isNull();
        AbstractLink linkAddNewElement = ListTestUtil.findMasterDetailLink(tester);
        Assert.assertTrue(!linkAddNewElement.isVisible() || !linkAddNewElement.isVisibleInHierarchy());
    }

    @Test
    public void verifyHaveDefaultsActions() {
        //Defaults actions: Edit Remove

        ISupplier<SViewListByMasterDetail> viewListByMasterDetail = (ISupplier<SViewListByMasterDetail>) SViewListByMasterDetail::new;

        tester.getDummyPage().setTypeBuilder(s -> ListTestUtil.buildTableForButons(s, viewListByMasterDetail));
        tester.startDummyPage();

        AbstractLink linkAddNewElement = ListTestUtil.findMasterDetailLink(tester);
        Assert.assertTrue(linkAddNewElement.isVisible() || linkAddNewElement.isVisibleInHierarchy());
        ListTestUtil.clickAddMasterDetailButton(tester);
        tester.getAssertionsForm().getSubComponentWithType(ListTestUtil.getBuildTableForButons()).assertSInstance().isList(2);

        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.EYE).isNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.PENCIL).isNotNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof IconeView && ((IconeView) b).getIcone() == DefaultIcons.REMOVE).isNotNull();
    }




}
