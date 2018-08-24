/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.circular.CircularReferenceExceptionHandler;
import de.danielbechler.diff.introspection.PropertyAccessExceptionHandler;
import de.danielbechler.diff.introspection.PropertyReadException;
import de.danielbechler.diff.node.DiffNode;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.serialize.java.JavaSerializer;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author Daniel C. Bordin on 03/04/2017.
 */
public class TabMapperTest {

    @Test
    public void test_Edit() {
        testTab(ctx -> ctx.getDummyPage().setAsEditView());
    }

    @Test
    public void test_Edit_Annotation() {
        testTab(ctx -> {
            ctx.getDummyPage().setAsEditView();
            ctx.getDummyPage().getSingularFormPanel().setAnnotationMode(AnnotationMode.EDIT);
        });
        testTab(ctx -> ctx.getDummyPage().setAsEditView());
    }

    @Test
    public void test_ReadOnly() {
        testTab(ctx -> ctx.getDummyPage().setAsVisualizationView());
    }

    @Test
    public void test_ReadOnly_Annotation() {
        testTab(ctx -> {
            ctx.getDummyPage().setAsVisualizationView();
            ctx.getDummyPage().getSingularFormPanel().setAnnotationMode(AnnotationMode.EDIT);
        });
    }

    @Test
    public void testLastTabAsDefault() {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleFormUsingTabDefault);
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);
        assertTabMenuActive(ctx, "experiencia", true);
        assertTabMenuActive(ctx, "informacoes", false);
        assertTabContent(assertionsTab, "experiencia");
    }

    @Test
    public void testTabSerialization() {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleForm);
        ctx.startDummyPage();

        JavaSerializer javaSerializer = new JavaSerializer("");
        System.out.println(Bytes.bytes(javaSerializer.serialize(ctx.getDummyPage()).length));

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);

        clickOnTab(ctx, assertionsTab, 1);
        assertTabMenuActive(ctx, "experiencia", true);
        assertTabContent(assertionsTab, "experiencia");

        byte[] oneClick = javaSerializer.serialize(ctx.getDummyPage());

        clickOnTab(ctx, assertionsTab, 1);
        assertTabMenuActive(ctx, "experiencia", true);
        assertTabContent(assertionsTab, "experiencia");

        byte[] twoClicks = javaSerializer.serialize(ctx.getDummyPage());

        DummyPage pageOneClick = (DummyPage) javaSerializer.deserialize(oneClick);

        DummyPage pageTwoClicks = (DummyPage) javaSerializer.deserialize(twoClicks);

        System.out.println("Equals: " + pageOneClick.equals(pageTwoClicks));

        for (int i = 0; i < 100; i++) {
            clickOnTab(ctx, assertionsTab, 1);
            assertTabMenuActive(ctx, "experiencia", true);
            assertTabContent(assertionsTab, "experiencia");

            System.out.println(Bytes.bytes(javaSerializer.serialize(ctx.getDummyPage()).length));
        }
    }

    public void testTab(Consumer<SingularFormDummyPageTester> config) {
        SingularFormDummyPageTester ctx = new SingularFormDummyPageTester();
        ctx.getDummyPage().setTypeBuilder(TabMapperTest::createSimpleForm);
        config.accept(ctx);
        ctx.startDummyPage();

        AssertionsWComponent assertionsTab = getAssertionsTab(ctx);
        assertTabMenuActive(ctx, "informacoes", true);
        assertTabContent(assertionsTab, "nome", "idade");

        clickOnTab(ctx, assertionsTab, 1);
        assertTabMenuActive(ctx, "experiencia", true);
        assertTabContent(assertionsTab, "experiencia");

        clickOnTab(ctx, assertionsTab, 0);
        assertTabMenuActive(ctx, "informacoes", true);
        assertTabContent(assertionsTab, "nome", "idade");
    }

    private void clickOnTab(SingularFormDummyPageTester ctx, AssertionsWComponent assertionsTab, int tabIndex) {
        ctx.clickLink(assertionsTab.getSubComponentWithId("tab").getSubComponentsWithId("tabAnchor").element(tabIndex)
                .getTarget());
    }

    private void assertTabMenuActive(SingularFormDummyPageTester ctx, String tabName, boolean expected) {
        Assert.assertEquals(String.format("Tab %s is not active on the menu.", tabName), expected,
                TagTester.createTagByAttribute(ctx.getLastResponseAsString(), "data-tab-name", tabName)
                        .getAttributeContains("class", "active")
        );
    }

    private void assertTabContent(AssertionsWComponent assertionsTab, String... expectedInstancesName) {
        AssertionsWComponent content = assertionsTab.getSubComponentWithId("tab-content").isNotNull();
        content.getSubComponentsWithSInstance().hasSize(expectedInstancesName.length);
        for (String name : expectedInstancesName) {
            content.getSubComponentWithTypeNameSimple(name).isNotNull();
        }
    }

    @Nonnull
    private AssertionsWComponent getAssertionsTab(SingularFormDummyPageTester ctx) {
        AssertionsWComponent assertionsTab = ctx.getAssertionsPage().getSubComponentForSInstance(
                ctx.getAssertionsInstance().getTarget());
        Assert.assertEquals(SViewTab.class, assertionsTab.assertSInstance().getTarget().getType().getView().getClass());
        return assertionsTab;
    }

    private static void createSimpleForm(STypeComposite testForm) {

        STypeString  nome  = testForm.addFieldString("nome");
        STypeInteger idade = testForm.addFieldInteger("idade");

        STypeComposite experiencia = testForm.addFieldComposite("experiencia");
        STypeString    empresa     = experiencia.addFieldString("empresa", true);
        STypeString    cargo       = experiencia.addFieldString("cargo", true);
        experiencia.asAtr().label("Experiencias");
        experiencia.asAtrAnnotation().setAnnotated();


        SViewTab tabbed = new SViewTab();
        tabbed.addTab("informacoes", "Informações pessoais")
                .add(nome)
                .add(idade);
        tabbed.addTab(experiencia);
        testForm.withView(tabbed);
    }

    private static void createSimpleFormUsingTabDefault(STypeComposite sTypeComposite) {
        createSimpleForm(sTypeComposite);
        SViewTab viewTab = (SViewTab) sTypeComposite.getView();
        viewTab.getTabs().get(1).setDefault();
    }
}
