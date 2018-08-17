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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.list.ButtonsConfig;
import org.opensingular.form.view.list.SViewListByForm;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.form.wicket.mapper.AbstractListMapper;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

public class PanelListButtonsTest {

    private static STypeList<STypeString, SIString> disableAllButons;
    private static STypeList<STypeString, SIString> enableAllButton;

    private SingularFormDummyPageTester tester;

    private static void buildDisableAllButtons(STypeComposite<?> mockType){
        disableAllButons = mockType.addFieldListOf("nomes", STypeString.class);

        disableAllButons.withView(new SViewListByForm()
                .configureDeleteButtonPerRow(f -> false)
                .disableNew());

        PanelListButtonsTest.fillWithBlankValues(disableAllButons);

        disableAllButons.asAtr().label("Nomes");
    }
    private static void buildEnableAllButtons(STypeComposite<?> mockType){
        enableAllButton = mockType.addFieldListOf("nomes", STypeString.class);

        enableAllButton.withView(new SViewListByForm()
                .configureEditButtonPerRow(ButtonsConfig.EDITAR_HINT, null, DefaultIcons.PUZZLE, true));

        PanelListButtonsTest.fillWithBlankValues(enableAllButton);

        enableAllButton.asAtr().label("Nomes");
    }

    private static void fillWithBlankValues(STypeList<STypeString, SIString> element) {
        element.withInitListener(list -> list.addNew().setValue("01"));
    }

    @Before
    public void setUp(){
        tester = new SingularFormDummyPageTester();
    }

    @Test
    public void verifyDontHaveActionButton() {
        tester.getDummyPage().setTypeBuilder(PanelListButtonsTest::buildDisableAllButtons);
        tester.startDummyPage();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof AbstractListMapper.InserirButton).isNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof AbstractListMapper.RemoverButton).isNull();
        AbstractLink linkAddNewElement = findAddButton();
        Assert.assertTrue(!linkAddNewElement.isVisible() || !linkAddNewElement.isVisibleInHierarchy());
    }
    @Test
    public void verifyHaveAllActionButtons() {
        //Table List contains 3 buttons : Edit, New, Remove
        tester.getDummyPage().setTypeBuilder(PanelListButtonsTest::buildEnableAllButtons);
        tester.startDummyPage();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof AbstractListMapper.InserirButton).isNotNull();
        tester.getAssertionsForm().findSubComponent(b -> b instanceof AbstractListMapper.RemoverButton).isNotNull();
        AbstractLink linkAddNewElement = findAddButton();
        Assert.assertTrue(linkAddNewElement.isVisible() || linkAddNewElement.isVisibleInHierarchy());
    }

    public AjaxLink findAddButton(){
        return tester.getAssertionsForm().findSubComponent(b -> b.getClass().getName().contains("AddButton")).getTarget(AjaxLink.class);
    }
}
